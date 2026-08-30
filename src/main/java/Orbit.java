import java.nio.file.Paths;
import java.util.ArrayList;
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
     * @param task task to add
     * @param storage persistent task storage
     * @throws OrbitException if the updated task list cannot be saved
     */
    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage) throws OrbitException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (OrbitException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Returns the trimmed text after a command word.
     *
     * @param input trimmed user input
     * @param command command word at the start of the input
     * @return command arguments, or an empty string when there are none
     */
    private static String getArguments(String input, String command) {
        return input.substring(command.length()).trim();
    }

    /**
     * Rejects arguments supplied to a command that does not accept any.
     *
     * @param arguments text after the command word
     * @throws OrbitException if any argument was supplied
     */
    private static void requireNoArguments(String arguments) throws OrbitException {
        if (!arguments.isEmpty()) {
            throw new OrbitException("I don't know that command.");
        }
    }

    /**
     * Finds one standalone marker token while rejecting duplicate occurrences.
     *
     * @param text text that may contain the marker
     * @param marker marker token, including its leading slash
     * @return the marker index, {@code -1} when absent, or {@code -2} when duplicated
     */
    private static int findOnlyMarker(String text, String marker) {
        int markerIndex = -1;
        int searchIndex = 0;
        while (searchIndex < text.length()) {
            int candidateIndex = text.indexOf(marker, searchIndex);
            if (candidateIndex < 0) {
                break;
            }
            int candidateEnd = candidateIndex + marker.length();
            boolean hasLeftBoundary = candidateIndex == 0
                    || Character.isWhitespace(text.charAt(candidateIndex - 1));
            boolean hasRightBoundary = candidateEnd == text.length()
                    || Character.isWhitespace(text.charAt(candidateEnd));
            if (hasLeftBoundary && hasRightBoundary) {
                if (markerIndex >= 0) {
                    return -2;
                }
                markerIndex = candidateIndex;
            }
            searchIndex = candidateEnd;
        }
        return markerIndex;
    }

    /**
     * Parses and validates a one-based task number.
     *
     * @param arguments text after a mark, unmark, or delete command
     * @param taskCount number of stored tasks
     * @return the corresponding zero-based task index
     * @throws OrbitException if the argument is missing, malformed, or out of range
     */
    private static int parseTaskIndex(String arguments, int taskCount) throws OrbitException {
        if (arguments.isEmpty() || arguments.split("\\s+").length != 1) {
            throw new OrbitException("Please provide exactly one task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new OrbitException("Please provide a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OrbitException("Task number " + taskNumber + " is out of range.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses a todo only after confirming that its description is present.
     *
     * @param arguments text after the todo command
     * @return a validated todo
     * @throws OrbitException if the description is empty
     */
    private static Todo parseTodo(String arguments) throws OrbitException {
        if (arguments.isEmpty()) {
            throw new OrbitException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Parses a deadline in the form {@code deadline DESCRIPTION /by DATE_OR_TIME}.
     *
     * @param arguments text after the deadline command
     * @return a validated deadline
     * @throws OrbitException if a required field or marker is missing
     */
    private static Deadline parseDeadline(String arguments) throws OrbitException {
        String marker = "/by";
        int markerIndex = findOnlyMarker(arguments, marker);
        if (markerIndex < 0) {
            throw new OrbitException("Use: deadline <description> /by <date or time>.");
        }

        String description = arguments.substring(0, markerIndex).trim();
        String by = arguments.substring(markerIndex + marker.length()).trim();
        if (description.isEmpty()) {
            throw new OrbitException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new OrbitException("The date or time of a deadline cannot be empty.");
        }
        return new Deadline(description, by);
    }

    /**
     * Parses an event in the form {@code event DESCRIPTION /from START /to END}.
     *
     * @param arguments text after the event command
     * @return a validated event
     * @throws OrbitException if a required field or marker is missing
     */
    private static Event parseEvent(String arguments) throws OrbitException {
        String fromMarker = "/from";
        String toMarker = "/to";
        int fromIndex = findOnlyMarker(arguments, fromMarker);
        int toIndex = findOnlyMarker(arguments, toMarker);
        boolean hasOneOrderedMarkerPair = fromIndex >= 0 && toIndex > fromIndex;
        if (!hasOneOrderedMarkerPair) {
            throw new OrbitException("Use: event <description> /from <start> /to <end>.");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + fromMarker.length(), toIndex).trim();
        String to = arguments.substring(toIndex + toMarker.length()).trim();
        if (description.isEmpty()) {
            throw new OrbitException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new OrbitException("The start of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new OrbitException("The end of an event cannot be empty.");
        }
        return new Event(description, from, to);
    }

    /**
     * Executes one command after validating all user-controlled values.
     *
     * @param input trimmed user input
     * @param tasks task storage
     * @param storage persistent task storage
     * @return false only when the user enters a valid bye command
     * @throws OrbitException if the command or any of its arguments is invalid
     */
    private static boolean handleCommand(String input, ArrayList<Task> tasks, Storage storage) throws OrbitException {
        if (input.isEmpty()) {
            throw new OrbitException("Please enter a command.");
        }

        CommandType commandType = CommandType.fromInput(input);
        String arguments = getArguments(input, commandType.getKeyword());
        switch (commandType) {
        case BYE:
            requireNoArguments(arguments);
            System.out.println("Bye. Hope to see you again soon!");
            return false;
        case LIST:
            requireNoArguments(arguments);
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
            return true;
        case MARK:
            int taskIndex = parseTaskIndex(arguments, tasks.size());
            Task task = tasks.get(taskIndex);
            boolean wasDone = task.isDone();
            task.markAsDone();
            try {
                storage.save(tasks);
            } catch (OrbitException exception) {
                if (!wasDone) {
                    task.markAsNotDone();
                }
                throw exception;
            }
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + task);
            return true;
        case UNMARK:
            taskIndex = parseTaskIndex(arguments, tasks.size());
            task = tasks.get(taskIndex);
            wasDone = task.isDone();
            task.markAsNotDone();
            try {
                storage.save(tasks);
            } catch (OrbitException exception) {
                if (wasDone) {
                    task.markAsDone();
                }
                throw exception;
            }
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + task);
            return true;
        case DELETE:
            taskIndex = parseTaskIndex(arguments, tasks.size());
            Task removedTask = tasks.remove(taskIndex);
            try {
                storage.save(tasks);
            } catch (OrbitException exception) {
                tasks.add(taskIndex, removedTask);
                throw exception;
            }
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
            return true;
        case TODO:
            addTask(tasks, parseTodo(arguments), storage);
            return true;
        case DEADLINE:
            addTask(tasks, parseDeadline(arguments), storage);
            return true;
        case EVENT:
            addTask(tasks, parseEvent(arguments), storage);
            return true;
        default:
            throw new OrbitException("I don't know that command.");
        }
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
        Storage storage = new Storage(Paths.get("data", "orbit.txt"));
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (OrbitException exception) {
            System.out.println("OOPS! " + exception.getMessage());
            tasks = new ArrayList<>();
        }
        boolean shouldContinue = true;
        while (shouldContinue && scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            try {
                shouldContinue = handleCommand(input, tasks, storage);
            } catch (OrbitException exception) {
                System.out.println("OOPS! " + exception.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }
}
