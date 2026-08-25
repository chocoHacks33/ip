/**
 * Identifies the commands understood by Orbit.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete");

    private final String keyword;

    /**
     * Creates a command type for its console keyword.
     *
     * @param keyword command word entered by the user
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command word entered by the user.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Checks whether an input starts with this exact command word.
     *
     * @param input trimmed user input
     * @return true if this command word matches exactly
     */
    private boolean matches(String input) {
        return input.equals(keyword)
                || (input.startsWith(keyword)
                && input.length() > keyword.length()
                && Character.isWhitespace(input.charAt(keyword.length())));
    }

    /**
     * Resolves the command type represented by an input line.
     *
     * @param input trimmed user input
     * @return matching command type
     * @throws OrbitException if the command word is unknown
     */
    public static CommandType fromInput(String input) throws OrbitException {
        for (CommandType commandType : values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        throw new OrbitException("I don't know that command.");
    }
}
