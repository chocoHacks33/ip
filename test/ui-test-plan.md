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

## TC2: Reject unknown commands and continue

Aim: Verify blank, unknown, and near-miss commands report errors without exiting or creating tasks.

### Input

```text
blah

todofoo
list extra
bye now
todo recovered
list
bye
```

### Expected output (ordered fragments)

```text
OOPS! I don't know that command.
OOPS! Please enter a command.
OOPS! I don't know that command.
OOPS! I don't know that command.
OOPS! I don't know that command.
[T][ ] recovered
Now you have 1 tasks in the list.
1.[T][ ] recovered
Bye. Hope to see you again soon!
```

## TC3: Reject malformed typed tasks

Aim: Verify missing descriptions, markers, dates, and times are handled without terminating Orbit.

### Input

```text
todo
deadline
deadline write report
deadline /by Sunday
deadline write report /by
deadline write report /by Sunday /by Monday
event
event meeting /from Monday
event meeting /to Tuesday
event meeting /to Tuesday /from Monday
event /from Monday /to Tuesday
event meeting /from /to Tuesday
event meeting /from Monday /to
todo valid task
list
bye
```

### Expected output (ordered fragments)

```text
OOPS! The description of a todo cannot be empty.
OOPS! Use: deadline <description> /by <date or time>.
OOPS! Use: deadline <description> /by <date or time>.
OOPS! The description of a deadline cannot be empty.
OOPS! The date or time of a deadline cannot be empty.
OOPS! Use: deadline <description> /by <date or time>.
OOPS! Use: event <description> /from <start> /to <end>.
OOPS! Use: event <description> /from <start> /to <end>.
OOPS! Use: event <description> /from <start> /to <end>.
OOPS! Use: event <description> /from <start> /to <end>.
OOPS! The description of an event cannot be empty.
OOPS! The start of an event cannot be empty.
OOPS! The end of an event cannot be empty.
[T][ ] valid task
Now you have 1 tasks in the list.
1.[T][ ] valid task
Bye. Hope to see you again soon!
```

## TC4: Preserve task state across index errors

Aim: Verify invalid mark and unmark commands do not mutate tasks and valid commands still work afterward.

### Input

```text
todo alpha
deadline beta /by Friday
mark 2
mark
mark abc
mark 1 2
mark 0
unmark 3
deadline broken
event broken /from Monday
list
unmark 2
list
bye
```

### Expected output (ordered fragments)

```text
[T][ ] alpha
Now you have 1 tasks in the list.
[D][ ] beta (by: Friday)
Now you have 2 tasks in the list.
[D][X] beta (by: Friday)
OOPS! Please provide exactly one task number.
OOPS! Please provide a valid task number.
OOPS! Please provide exactly one task number.
OOPS! Task number 0 is out of range.
OOPS! Task number 3 is out of range.
OOPS! Use: deadline <description> /by <date or time>.
OOPS! Use: event <description> /from <start> /to <end>.
1.[T][ ] alpha
2.[D][X] beta (by: Friday)
[D][ ] beta (by: Friday)
1.[T][ ] alpha
2.[D][ ] beta (by: Friday)
Bye. Hope to see you again soon!
```

## TC5: Mark and unmark a typed task

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

## TC6: Preserve arbitrary deadline text

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
