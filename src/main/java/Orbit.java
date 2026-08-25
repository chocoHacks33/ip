import java.util.Scanner;

/**
 * Entry point for Orbit, a friendly command-line task assistant.
 */
public class Orbit {
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Stores a task and reports the updated task count.
     *
     * @param tasks task storage
     * @param taskCount number of tasks currently stored
     * @param task task to add
     * @return the updated task count
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + newTaskCount + " tasks in the list.");
        return newTaskCount;
    }

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
        Task[] tasks = new Task[100];
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
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(input.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (input.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(input.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskIndex]);
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                taskCount = addTask(tasks, taskCount, new Todo(description));
            } else if (input.startsWith("deadline ")) {
                String details = input.substring(9);
                int byMarker = details.indexOf(" /by ");
                String description = details.substring(0, byMarker);
                String by = details.substring(byMarker + 5);
                taskCount = addTask(tasks, taskCount, new Deadline(description, by));
            } else if (input.startsWith("event ")) {
                String details = input.substring(6);
                int fromMarker = details.indexOf(" /from ");
                int toMarker = details.indexOf(" /to ", fromMarker + 7);
                String description = details.substring(0, fromMarker);
                String from = details.substring(fromMarker + 7, toMarker);
                String to = details.substring(toMarker + 5);
                taskCount = addTask(tasks, taskCount, new Event(description, from, to));
            } else {
                System.out.println("I'm sorry, but I don't know what that means.");
            }
            System.out.println(SEPARATOR);
        }
    }
}
