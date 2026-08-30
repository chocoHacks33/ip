/**
 * Represents a task that takes place between two stated times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     *
     * @param description description shown to the user
     * @param from raw start text supplied by the user
     * @param to raw end text supplied by the user
     */
    public Event(String description, String from, String to) {
        this(description, from, to, false);
    }

    /**
     * Creates an event with a stored completion status.
     *
     * @param description description shown to the user
     * @param from raw start text supplied by the user
     * @param to raw end text supplied by the user
     * @param isDone whether the event is completed
     */
    public Event(String description, String from, String to, boolean isDone) {
        super(description, isDone);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start text.
     *
     * @return raw start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end text.
     *
     * @return raw end text
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
