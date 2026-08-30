package orbit.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_validDeadline_returnsStructuredCommand() throws OrbitException {
        ParsedCommand command = parser.parse("deadline submit report /by 2028-02-29");

        assertEquals(CommandType.DEADLINE, command.getType());
        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2028, 2, 29), deadline.getBy());
    }

    @Test
    void parse_validEvent_returnsBothDates() throws OrbitException {
        ParsedCommand command = parser.parse(
                "event orientation /from 2026-09-01 /to 2026-09-02");

        assertEquals(CommandType.EVENT, command.getType());
        Event event = assertInstanceOf(Event.class, command.getTask());
        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 1), event.getFrom());
        assertEquals(LocalDate.of(2026, 9, 2), event.getTo());
    }

    @Test
    void parse_impossibleDate_throwsHelpfulError() {
        OrbitException exception = assertThrows(OrbitException.class,
                () -> parser.parse("deadline impossible /by 2026-02-29"));

        assertEquals("Please enter dates as yyyy-MM-dd.", exception.getMessage());
    }

    @Test
    void parse_duplicateMarker_rejectsCommand() {
        OrbitException exception = assertThrows(OrbitException.class,
                () -> parser.parse("deadline report /by 2026-09-01 /by 2026-09-02"));

        assertEquals("Use: deadline <description> /by <yyyy-MM-dd>.",
                exception.getMessage());
    }
}
