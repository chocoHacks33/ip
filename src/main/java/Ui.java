import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Reads console input and presents Orbit's user-facing messages.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the process console.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI connected to the given streams.
     *
     * @param input command input stream
     * @param output response output stream
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Checks whether another command is available.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command without changing its meaningful content.
     *
     * @return next input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Orbit's greeting.
     */
    public void showWelcome() {
        String banner = "  ___       _     _ _   \n"
                + " / _ \\ _ __| |__ (_) |_ \n"
                + "| | | | '__| '_ \\| | __|\n"
                + "| |_| | |  | |_) | | |_ \n"
                + " \\___/|_|  |_.__/|_|\\__|\n";
        output.println(banner);
        output.println(SEPARATOR);
        output.println("Hello! I'm Orbit.");
        output.println("What can I do for you?");
        output.println(SEPARATOR);
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        output.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a task that was added.
     *
     * @param task added task
     * @param taskCount updated task count
     */
    public void showAddedTask(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the complete numbered task list.
     *
     * @param tasks tasks in display order
     */
    public void showTaskList(List<Task> tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays a task that was marked as done.
     *
     * @param task updated task
     */
    public void showMarkedTask(Task task) {
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
    }

    /**
     * Displays a task that was marked as not done.
     *
     * @param task updated task
     */
    public void showUnmarkedTask(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println("  " + task);
    }

    /**
     * Displays a task that was deleted.
     *
     * @param task removed task
     * @param taskCount updated task count
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays an error that the user can act on.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        output.println("OOPS! " + message);
    }

    /**
     * Displays a separator after a command response.
     */
    public void showSeparator() {
        output.println(SEPARATOR);
    }
}
