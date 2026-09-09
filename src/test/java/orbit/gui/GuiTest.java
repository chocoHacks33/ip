package orbit.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import orbit.ChatSession;

/**
 * Exercises the real FXML scene and controls on the JavaFX application thread.
 * These desktop tests are opt-in through the separate guiTest Gradle task.
 */
@Tag("gui")
@Timeout(30)
class GuiTest {
    private static final int FX_TIMEOUT_SECONDS = 10;

    @TempDir
    Path tempDirectory;

    private final AtomicInteger exitRequests = new AtomicInteger();
    private Stage stage;
    private Parent root;
    private TextField userInput;
    private Button sendButton;
    private ScrollPane scrollPane;
    private VBox dialogContainer;

    @BeforeAll
    static void startToolkit() throws Exception {
        Path implementation = Path.of(MainWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        assertEquals(Path.of("build", "libs", "Orbit.jar").toRealPath(), implementation.toRealPath(),
                "GUI tests must exercise the packaged Orbit.jar rather than loose application classes.");
        CompletableFuture<Void> started = new CompletableFuture<>();
        Runnable initialize = () -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        };
        try {
            Platform.startup(initialize);
        } catch (IllegalStateException exception) {
            // Another GUI test may have initialized the toolkit in this worker.
            Platform.runLater(initialize);
        }
        started.get(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopToolkit() {
        Platform.exit();
    }

    @BeforeEach
    void showWindow() throws Exception {
        onFx(() -> {
            FXMLLoader loader = new FXMLLoader(GuiTest.class.getResource("/view/MainWindow.fxml"));
            root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSession(new ChatSession(tempDirectory.resolve("orbit.txt")),
                    exitRequests::incrementAndGet);
            userInput = (TextField) root.lookup("#userInput");
            sendButton = (Button) root.lookup("#sendButton");
            scrollPane = (ScrollPane) root.lookup("#scrollPane");
            assertNotNull(userInput);
            assertNotNull(sendButton);
            assertNotNull(scrollPane);
            dialogContainer = (VBox) scrollPane.getContent();
            assertNotNull(dialogContainer);
            assertEquals("dialogContainer", dialogContainer.getId());

            stage = new Stage();
            stage.setTitle("Orbit GUI integration test");
            stage.setScene(new Scene(root, 760, 680));
            stage.show();
            return null;
        });
        awaitLayout();
    }

    @AfterEach
    void closeWindow() throws Exception {
        onFx(() -> {
            if (stage != null) {
                stage.close();
            }
            return null;
        });
    }

    @Test
    void submit_enterAndSend_echoCommandsAndShowResponses() throws Exception {
        int initialCount = onFx(() -> dialogContainer.getChildren().size());
        assertTrue(onFx(() -> messageText(initialCount - 1).contains("Orbit")));

        send("todo Read CS2103 notes", false);
        onFx(() -> {
            assertEquals(initialCount + 2, dialogContainer.getChildren().size());
            assertEquals("todo Read CS2103 notes", messageText(initialCount));
            assertTrue(messageText(initialCount + 1).contains("[T][ ] Read CS2103 notes"));
            assertEquals("", userInput.getText());
            assertSame(userInput, stage.getScene().getFocusOwner());
            return null;
        });

        send("list", true);
        onFx(() -> {
            assertEquals(initialCount + 4, dialogContainer.getChildren().size());
            assertEquals("list", messageText(initialCount + 2));
            assertTrue(messageText(initialCount + 3).contains("1.[T][ ] Read CS2103 notes"));
            assertEquals("", userInput.getText());
            assertSame(userInput, stage.getScene().getFocusOwner());
            return null;
        });
    }

    @Test
    void submit_invalidAndBlankCommands_recoversWithoutEmptyUserBubble() throws Exception {
        int initialCount = onFx(() -> dialogContainer.getChildren().size());
        send("unknown-command", true);
        onFx(() -> {
            assertEquals(initialCount + 2, dialogContainer.getChildren().size());
            assertTrue(lastMessage().contains("OOPS! I don't know that command."));
            return null;
        });

        send("   ", false);
        onFx(() -> {
            assertEquals(initialCount + 3, dialogContainer.getChildren().size());
            assertTrue(lastMessage().contains("OOPS! Please enter a command."));
            assertEquals("", userInput.getText());
            assertSame(userInput, stage.getScene().getFocusOwner());
            assertFalse(sendButton.isDisabled());
            return null;
        });

        send("deadline invalid date /by 2026-02-29", true);
        onFx(() -> {
            assertEquals(initialCount + 5, dialogContainer.getChildren().size());
            assertTrue(lastMessage().contains("OOPS! Please enter dates as yyyy-MM-dd."));
            assertEquals("", userInput.getText());
            return null;
        });

        send("bye now", false);
        onFx(() -> {
            assertEquals(initialCount + 7, dialogContainer.getChildren().size());
            assertTrue(lastMessage().contains("OOPS! I don't know that command."));
            assertFalse(userInput.isDisabled());
            assertFalse(sendButton.isDisabled());
            assertEquals(0, exitRequests.get());
            return null;
        });

        send("todo recovered", false);
        send("list", true);
        onFx(() -> {
            assertEquals(initialCount + 11, dialogContainer.getChildren().size());
            assertTrue(lastMessage().contains("1.[T][ ] recovered"));
            assertEquals(0, exitRequests.get());
            return null;
        });
    }

    @Test
    void submit_bye_disablesInputAndInvokesExitCallback() throws Exception {
        send("bye", true);
        onFx(() -> {
            assertTrue(lastMessage().contains("Bye. Hope to see you again soon!"));
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
            assertEquals(1, exitRequests.get());
            return null;
        });
    }

    @Test
    void layout_longConversationAndResize_wrapsMessagesAndScrollsToLatest() throws Exception {
        String longDescription = "Review the GUI layout with a detailed task description and several useful notes. "
                .repeat(10).strip();
        onFx(() -> {
            for (int i = 1; i <= 12; i++) {
                userInput.setText("todo Task " + i);
                sendButton.fire();
            }
            userInput.setText("todo " + longDescription);
            sendButton.fire();
            return null;
        });
        awaitLayout();
        double wideHeight = onFx(() -> lastDialog().getHeight());
        double wideSceneWidth = onFx(() -> stage.getScene().getWidth());

        onFx(() -> {
            stage.setWidth(480);
            stage.setHeight(440);
            return null;
        });
        awaitLayout();
        onFx(() -> {
            assertTrue(stage.getScene().getWidth() < wideSceneWidth);
            assertTrue(lastDialog().getHeight() > wideHeight, "Narrower bubbles should wrap onto more lines.");
            assertTrue(dialogContainer.getHeight() > scrollPane.getViewportBounds().getHeight());
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue(), 0.01,
                    "The latest response should remain at the bottom of the conversation.");

            Node viewport = scrollPane.lookup(".viewport");
            assertNotNull(viewport);
            Bounds viewportBounds = viewport.localToScene(viewport.getBoundsInLocal());
            for (Node message : dialogContainer.getChildren()) {
                Label dialog = (Label) message.lookup("#dialog");
                Label avatar = (Label) message.lookup("#avatar");
                assertNotNull(dialog);
                assertNotNull(avatar);
                assertTrue(dialog.isWrapText());
                Bounds dialogBounds = dialog.localToScene(dialog.getBoundsInLocal());
                assertTrue(dialogBounds.getMinX() >= viewportBounds.getMinX() - 1,
                        "A message must not overflow the viewport's left edge.");
                assertTrue(dialogBounds.getMaxX() <= viewportBounds.getMaxX() + 1,
                        "A message must not overflow the viewport's right edge.");
            }
            return null;
        });
    }

    @Test
    void snapshot_demoConversation_writesActualSceneImage() throws Exception {
        onFx(() -> {
            stage.setWidth(840);
            stage.setHeight(860);
            return null;
        });
        send("todo Read CS2103 notes", true);
        send("deadline Submit iP update /by 2026-09-04", false);
        send("event CS2103 tutorial /from 2026-09-04 /to 2026-09-04", true);
        send("mark 1", false);
        send("sort", false);
        send("list", true);
        assertTrue(onFx(() -> lastMessage().contains("1.[D][ ] Submit iP update")));
        assertTrue(onFx(() -> lastMessage().contains("3.[T][X] Read CS2103 notes")));

        WritableImage snapshot = onFx(() -> stage.getScene().snapshot(null));
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixels = snapshot.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, pixels.getArgb(x, y));
            }
        }
        Path screenshot = Path.of("build", "screenshots", "Orbit-GUI.png");
        Files.createDirectories(screenshot.getParent());
        assertTrue(ImageIO.write(image, "png", screenshot.toFile()));
        assertTrue(width > 600 && height > 500, "Capture the shown scene at a readable size.");
        assertTrue(Files.size(screenshot) > 10_000, "The rendered conversation should produce a nonempty PNG.");
    }

    /**
     * Sends input through one of the actual controls, then waits for its deferred layout and focus updates.
     */
    private void send(String command, boolean useEnter) throws Exception {
        onFx(() -> {
            userInput.setText(command);
            if (useEnter) {
                userInput.fireEvent(new ActionEvent());
            } else {
                sendButton.fire();
            }
            return null;
        });
        awaitLayout();
    }

    private Label lastDialog() {
        Node lastBox = dialogContainer.getChildren().getLast();
        return (Label) lastBox.lookup("#dialog");
    }

    private String lastMessage() {
        return lastDialog().getText();
    }

    private String messageText(int index) {
        Node box = dialogContainer.getChildren().get(index);
        Label dialog = (Label) box.lookup("#dialog");
        assertNotNull(dialog);
        return dialog.getText();
    }

    /**
     * Waits for real animation pulses rather than relying on arbitrary sleeps for native resize events.
     */
    private void awaitLayout() throws Exception {
        CompletableFuture<Void> rendered = new CompletableFuture<>();
        onFx(() -> {
            new AnimationTimer() {
                private int remainingPulses = 2;

                @Override
                public void handle(long now) {
                    root.applyCss();
                    root.layout();
                    if (--remainingPulses == 0) {
                        stop();
                        rendered.complete(null);
                    }
                }
            }.start();
            return null;
        });
        rendered.get(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Runs an operation on JavaFX's application thread and propagates failures with a bounded wait.
     */
    private static <T> T onFx(Callable<T> operation) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return operation.call();
        }
        FutureTask<T> result = new FutureTask<>(operation);
        Platform.runLater(result);
        return result.get(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
