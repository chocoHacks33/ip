package orbit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import orbit.task.Todo;

/**
 * Checks that varargs-based messages retain their exact text and line endings.
 */
class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(InputStream.nullInputStream(),
            new PrintStream(output, true, StandardCharsets.UTF_8));

    @Test
    void showGoodbye_singleLine_preservesExactOutput() {
        ui.showGoodbye();

        assertEquals("Bye. Hope to see you again soon!" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMarkedTask_twoLines_preservesSpacingAndUnicode() {
        Todo task = new Todo("read café notes");
        task.markAsDone();

        ui.showMarkedTask(task);

        String expected = "Nice! I've marked this task as done:" + System.lineSeparator()
                + "  [T][X] read café notes" + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showAddedTask_threeLines_preservesTextAndOrder() {
        ui.showAddedTask(new Todo("read book"), 3);

        String expected = "Got it. I've added this task:" + System.lineSeparator()
                + "  [T][ ] read book" + System.lineSeparator()
                + "Now you have 3 tasks in the list." + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }
}
