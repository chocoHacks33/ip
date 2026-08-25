# Orbit UI Test Plan

Run each case as an independent Orbit session. Expected lines are matched as ordered fragments so optional banners, separators, and indentation can change without making the tests brittle.

## TC1: Add and list every task type

Aim: Verify todos, deadlines, and events retain their type markers and raw date/time text.

### Input

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] borrow book
Now you have 1 tasks in the list.
[D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## TC2: Mark and unmark a typed task

Aim: Verify completion changes are reflected without losing the task type.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] read book
[T][X] read book
[T][ ] read book
1.[T][ ] read book
Bye. Hope to see you again soon!
```

## TC3: Preserve arbitrary deadline text

Aim: Verify Week 2 treats date and time values as strings rather than parsing them.

### Input

```text
deadline do homework /by no idea :-p
list
bye
```

### Expected output (ordered fragments)

```text
[D][ ] do homework (by: no idea :-p)
1.[D][ ] do homework (by: no idea :-p)
Bye. Hope to see you again soon!
```
