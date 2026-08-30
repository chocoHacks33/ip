/**
 * Represents a task that should be completed by a stated date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description shown to the user
     * @param by raw deadline text supplied by the user
     */
    public Deadline(String description, String by) {
        this(description, by, false);
    }

    /**
     * Creates a deadline with a stored completion status.
     *
     * @param description description shown to the user
     * @param by raw deadline text supplied by the user
     * @param isDone whether the deadline is completed
     */
    public Deadline(String description, String by, boolean isDone) {
        super(description, isDone);
        this.by = by;
    }

    /**
     * Returns the deadline text.
     *
     * @return raw deadline text
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
