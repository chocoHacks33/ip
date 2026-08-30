package orbit.parser;

import orbit.task.Task;

/**
 * Contains the validated values needed to execute one Orbit command.
 */
public class ParsedCommand {
    private final CommandType type;
    private final Task task;
    private final Integer taskNumber;
    private final String keyword;

    private ParsedCommand(CommandType type, Task task, Integer taskNumber, String keyword) {
        this.type = type;
        this.task = task;
        this.taskNumber = taskNumber;
        this.keyword = keyword;
    }

    /**
     * Creates a command that has no arguments.
     *
     * @param type command type
     * @return parsed command
     */
    public static ParsedCommand withoutArguments(CommandType type) {
        return new ParsedCommand(type, null, null, null);
    }

    /**
     * Creates a command that adds a task.
     *
     * @param type command type
     * @param task task to add
     * @return parsed command
     */
    public static ParsedCommand withTask(CommandType type, Task task) {
        return new ParsedCommand(type, task, null, null);
    }

    /**
     * Creates a command that targets one task number.
     *
     * @param type command type
     * @param taskNumber one-based task number
     * @return parsed command
     */
    public static ParsedCommand withTaskNumber(CommandType type, int taskNumber) {
        return new ParsedCommand(type, null, taskNumber, null);
    }

    /**
     * Creates a command that carries a search keyword.
     *
     * @param type command type
     * @param keyword text to find in task descriptions
     * @return parsed command
     */
    public static ParsedCommand withKeyword(CommandType type, String keyword) {
        return new ParsedCommand(type, null, null, keyword);
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

    /**
     * Returns the keyword carried by a find command.
     *
     * @return search keyword, or null for another command kind
     */
    public String getKeyword() {
        return keyword;
    }
}
