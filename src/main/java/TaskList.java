import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns Orbit's ordered collection of tasks and its index validation.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns a read-only view of the stored tasks.
     *
     * @return stored tasks in display order
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task by its one-based display number.
     *
     * @param taskNumber one-based task number
     * @return selected task
     * @throws OrbitException if the task number is out of range
     */
    public Task get(int taskNumber) throws OrbitException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Appends a task.
     *
     * @param task task to append
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task by its one-based display number.
     *
     * @param taskNumber one-based task number
     * @return removed task
     * @throws OrbitException if the task number is out of range
     */
    public Task delete(int taskNumber) throws OrbitException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Restores a task at its former one-based display number.
     *
     * @param taskNumber one-based task number
     * @param task task to restore
     */
    public void restore(int taskNumber, Task task) {
        tasks.add(taskNumber - 1, task);
    }

    private int toIndex(int taskNumber) throws OrbitException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new OrbitException("Task number " + taskNumber + " is out of range.");
        }
        return taskNumber - 1;
    }
}
