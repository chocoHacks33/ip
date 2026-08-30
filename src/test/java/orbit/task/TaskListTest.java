package orbit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import orbit.exception.OrbitException;

class TaskListTest {
    @Test
    void delete_middleTask_removesOnlySelectedTask() throws OrbitException {
        Todo first = new Todo("first");
        Deadline second = new Deadline("second", LocalDate.of(2026, 9, 4));
        Event third = new Event("third", LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 9, 6));
        TaskList tasks = new TaskList(List.of(first, second, third));

        Task removed = tasks.delete(2);

        assertSame(second, removed);
        assertEquals(List.of(first, third), tasks.asList());
    }

    @Test
    void restore_deletedTask_returnsItToOriginalPosition() throws OrbitException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));
        Task removed = tasks.delete(2);

        tasks.restore(2, removed);

        assertEquals(List.of(first, second, third), tasks.asList());
    }

    @Test
    void get_outOfRangeNumber_throwsHelpfulError() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));

        OrbitException zeroError = assertThrows(OrbitException.class,
                () -> tasks.get(0));
        OrbitException largeError = assertThrows(OrbitException.class,
                () -> tasks.get(2));

        assertEquals("Task number 0 is out of range.", zeroError.getMessage());
        assertEquals("Task number 2 is out of range.", largeError.getMessage());
    }
}
