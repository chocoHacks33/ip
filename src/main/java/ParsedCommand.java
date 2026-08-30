/**
 * Contains the validated values needed to execute one Orbit command.
 */
public class ParsedCommand {
    private final CommandType type;
    private final Task task;
    private final Integer taskNumber;

    private ParsedCommand(CommandType type, Task task, Integer taskNumber) {
        this.type = type;
        this.task = task;
        this.taskNumber = taskNumber;
    }

    /**
     * Creates a command that has no arguments.
     *
     * @param type command type
     * @return parsed command
     */
    public static ParsedCommand withoutArguments(CommandType type) {
        return new ParsedCommand(type, null, null);
    }

    /**
     * Creates a command that adds a task.
     *
     * @param type command type
     * @param task task to add
     * @return parsed command
     */
    public static ParsedCommand withTask(CommandType type, Task task) {
        return new ParsedCommand(type, task, null);
    }

    /**
     * Creates a command that targets one task number.
     *
     * @param type command type
     * @param taskNumber one-based task number
     * @return parsed command
     */
    public static ParsedCommand withTaskNumber(CommandType type, int taskNumber) {
        return new ParsedCommand(type, null, taskNumber);
    }

    /**
     * Returns the command type.
     *
     * @return command type
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return task, or null for another command kind
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the one-based number carried by an indexed command.
     *
     * @return task number, or null for another command kind
     */
    public Integer getTaskNumber() {
        return taskNumber;
    }
}
