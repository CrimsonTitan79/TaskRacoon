import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains the task list, handles all operations to add/delete/modify tasks in the list
 */
public class TaskList {

    public static final int TASK_DESCRIPTION_INDEX = 0;
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * adds a task to the task list
     * Only called when the program is already running
     * not for adding tasks from DukeData/data.txt
     * Handles BlankDescriptionException
     *
     * @param taskType  TODO, DEADLINE or EVENT
     * @param userInput raw user input from standard input
     */
    public void addTaskPlusException(CommandEnum taskType, String userInput) {
        try {
            String userInputWithoutTaskCommand = Parser.stripCommandWord(taskType, userInput);
            addTask(taskType, userInputWithoutTaskCommand, false);

        } catch (BlankDescriptionException e) {
            Ui.printlnTab("☹ OOPS!!! The description of a task cannot be empty.");
            Ui.printDivider();
        }
    }

    /**
     * Attempts to add a task to task list and write it to DukeData/data.txt if successful
     *
     * @param taskType                    TODO, DEADLINE, EVENT
     * @param userInputWithoutTaskCommand userInput with command word removed
     * @param isDone                      boolean of whether task is Done or not
     */
    private void addTask(CommandEnum taskType, String userInputWithoutTaskCommand, boolean isDone) {

        try {
            String[] taskDetails; //array that should be of length 2 if strippedUserInput is valid
            switch (taskType) {
            case TODO:
                addTodo(userInputWithoutTaskCommand, isDone);
                break;
            case DEADLINE:
                taskDetails = Parser.getTaskDetails(CommandEnum.DEADLINE, userInputWithoutTaskCommand);
                addDeadline(taskDetails, isDone);
                break;
            case EVENT:
                taskDetails = Parser.getTaskDetails(CommandEnum.EVENT, userInputWithoutTaskCommand);
                addEvent(taskDetails, isDone);
            }
            addTaskSuccess();
            Storage.writeToFile();

        } catch (IncompleteInformationException e) {
            Ui.printlnTab("☹ OOPS!!! Please enter the right format for the task");
            Ui.printDivider();
        } catch (IOException e) {
            Ui.printlnTab("☹ OOPS!!! Error writing to data file");

        }
    }

    /**
     * Add a TODO to the task list
     *
     * @param description what the todo is about
     * @param isDone      boolean of whether the task is marked done or not
     */
    public void addTodo(String description, boolean isDone) {
        tasks.add(new Todo(description, isDone));
    }

    /**
     * Add a DEADLINE to the task list
     *
     * @param taskDetails array containing description and date
     * @param isDone      boolean of whether task is marked done or not
     */
    public void addDeadline(String[] taskDetails, boolean isDone) {
        tasks.add(new Deadline(taskDetails[TASK_DESCRIPTION_INDEX], isDone, taskDetails[Parser.TASK_DATE_INDEX]));
    }

    /**
     * Add an EVENT to the task list
     *
     * @param taskDetails array containing description and date
     * @param isDone      boolean of whether task is marked done or not
     */
    public void addEvent(String[] taskDetails, boolean isDone) {
        tasks.add(new Event(taskDetails[TASK_DESCRIPTION_INDEX], isDone, taskDetails[Parser.TASK_DATE_INDEX]));
    }

    /**
     * Reports that task has been successfully added
     * and prints the task and number of tasks
     */
    private void addTaskSuccess() {
        Ui.printlnTab("Got it. I've added this task:");
        Ui.printlnTab(" " + tasks.get(tasks.size() - 1)); //latest item
        printNumberOfTasksMessage();
        Ui.printDivider();
    }

    /**
     * Delete a particular task from the tasks list
     *
     * @param userInput a number that is greater than 0 and smaller than the number of tasks in the list
     * @throws BlankDescriptionException if taskNumberStr.isBlank()
     * @throws ExceedTotalTasksException if taskNumber > tasks.size()
     */
    public void deleteTask(String userInput) throws BlankDescriptionException, ExceedTotalTasksException {
        String taskNumberStr = Parser.stripCommandWord(CommandEnum.DELETE, userInput);
        if (taskNumberStr.isBlank()) {
            throw new BlankDescriptionException();
        }

        //taskNumber displayed starting with 1
        //but array starts with 0
        int taskNumber = Integer.parseInt(taskNumberStr);
        if (taskNumber > tasks.size()) {
            throw new ExceedTotalTasksException();
        }

        int taskIndex = taskNumber - 1;

        String taskToStringOutput = String.format(" %s", tasks.get(taskIndex));

        tasks.remove(taskIndex);

        Ui.printlnTab("Noted. I've removed this task:");
        Ui.printlnTab(taskToStringOutput);

        printNumberOfTasksMessage();
        Ui.printDivider();
    }

    /**
     * Mark a task as done
     *
     * @param userInput a number that is greater than 0 and smaller than the number of tasks in the list
     * @throws BlankDescriptionException if taskNumberStr.isBlank()
     * @throws ExceedTotalTasksException if taskNumber > tasks.size()
     */
    private void doneTask(String userInput) throws BlankDescriptionException, ExceedTotalTasksException {
        String taskNumberStr = Parser.stripCommandWord(CommandEnum.DONE, userInput);
        if (taskNumberStr.isBlank()) {
            throw new BlankDescriptionException();
        }
        //taskNumber displayed starting with 1
        //but array starts with 0
        int taskNumber = Integer.parseInt(taskNumberStr);
        if (taskNumber > tasks.size()) {
            throw new ExceedTotalTasksException();
        }
        int taskIndex = taskNumber - 1;

        tasks.get(taskIndex).markAsDone();
        Ui.printlnTab("Nice! I've marked this task as done:");
        Ui.printlnTab(String.format("%s", tasks.get(taskIndex)));
        Ui.printDivider();
    }

    /**
     * calls doneTask or deleteTask with exception handling
     *
     * @param userInput a number that is greater than 0 and smaller than the number of tasks in the list
     * @param COMMAND   DONE or DELETE
     */
    public void doneOrDeleteTaskPlusException(String userInput, String COMMAND) {
        try {
            if (COMMAND.equals(Parser.COMMAND_DONE)) {
                doneTask(userInput);
            } else {
                deleteTask(userInput);
            }
            Storage.writeToFile();

        } catch (BlankDescriptionException e) {
            Ui.printlnTab("☹ OOPS!!! Please enter a task number to complete");
            Ui.printDivider();

        } catch (NumberFormatException e) {
            Ui.printlnTab("☹ OOPS!!! Task number is not an integer.");
            Ui.printDivider();

        } catch (ExceedTotalTasksException e) {
            Ui.printlnTab("☹ OOPS!!! You only have " + tasks.size() + " tasks");
            Ui.printlnTab("Please enter a number smaller or equal to " + tasks.size());
            Ui.printDivider();

        } catch (IndexOutOfBoundsException e) {
            Ui.printlnTab("☹ OOPS!!! Please enter a task number greater than or equal to 1.");
            Ui.printDivider();

        } catch (IOException e) {
            Ui.printlnTab("☹ OOPS!!! Unable to write to " + Storage.dataPath);
            Ui.printDivider();
        }
    }



    public void findTasksPlusException(String userInput) {
        try {
            findTasks(userInput);
        } catch (BlankDescriptionException e) {
            Ui.printlnTab("☹ OOPS!!! Please enter some keywords to search your task list.");
            Ui.printDivider();
        }
    }

    private void findTasks(String userInput) throws BlankDescriptionException {
        if (tasks.isEmpty()) {
            Ui.printlnTab("Your task list is empty!");

        } else {
            String findKeyPhrase = Parser.stripCommandWord(CommandEnum.FIND, userInput);
            String findKeyPhraseLowerCase = findKeyPhrase.toLowerCase();
            if (findKeyPhrase.isBlank()) {
                throw new BlankDescriptionException();
            }

            String foundTasksString = "";
            for (int i = 0; i < tasks.size(); i++) {
                String taskDescriptionLowerCase = tasks.get(i).getDescription().toLowerCase();
                if (taskDescriptionLowerCase.contains(findKeyPhraseLowerCase)) {
                    foundTasksString += String.format("%d.%s", (i + 1), tasks.get(i)) + "\n\t";
                }
            }
            if (foundTasksString.isBlank()) {
                Ui.printlnTab("\"" + findKeyPhrase + "\"" + " not found in your list!");

            } else {
                Ui.printlnTab("Here are the matching tasks in your list:");
                Ui.printlnTab(foundTasksString);
            }
        }
        Ui.printDivider();
    }


    /**
     * List out all tasks from tasks list
     */
    public void listTasks() {
        if (tasks.isEmpty()) {
            Ui.printlnTab("Your task list is empty!");

        } else {
            Ui.printlnTab("Here are the tasks in your list:");

            //listing out tasks if userInput == "list"
            //tasks[] start with index 0
            //but printing out tasks starts with index 1
            for (int i = 0; i < tasks.size(); i++) {
                Ui.printlnTab(String.format("%d.%s", (i + 1), tasks.get(i)));
            }
        }
        Ui.printDivider();
    }

    /**
     * prints the total number of tasks in the list
     */
    private void printNumberOfTasksMessage() {
        if (tasks.size() == 1) {
            Ui.printlnTab("Now you have 1 task in the list.");
        } else {
            Ui.printlnTab(String.format("Now you have %d tasks in the list.", tasks.size()));
        }
    }

    /**
     * Concatenates the data storage formatted strings of all tasks
     *
     * @return data storage formatted strings of all tasks
     */
    public String getTasksDataStorageString() {
        String tasksDataStorageString = "";
        for (Task task : tasks) {
            tasksDataStorageString += task.getDataStorageString() + "\n";
        }
        return tasksDataStorageString;
    }
}

