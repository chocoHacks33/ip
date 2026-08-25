import java.util.Scanner;

/**
 * Entry point for Orbit, a friendly command-line task assistant.
 */
public class Orbit {
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Greets the user and stores tasks until the user enters {@code bye}.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        String banner = "  ___       _     _ _   \n"
                + " / _ \\ _ __| |__ (_) |_ \n"
                + "| | | | '__| '_ \\| | __|\n"
                + "| |_| | |  | |_) | | |_ \n"
                + " \\___/|_|  |_.__/|_|\\__|\n";
        System.out.println(banner);
        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Orbit.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] taskStatuses = new boolean[100];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = taskStatuses[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                taskStatuses[taskIndex] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[taskIndex]);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                taskStatuses[taskIndex] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  [ ] " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("added: " + input);
            }
            System.out.println(SEPARATOR);
        }
    }
}
