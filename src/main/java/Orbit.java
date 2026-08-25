/**
 * Entry point for Orbit, a friendly command-line task assistant.
 */
public class Orbit {
    private static final String SEPARATOR = "____________________________________________________________";

    /**
     * Greets the user and exits.
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
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
