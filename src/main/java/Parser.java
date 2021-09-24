import java.util.Scanner;

public class Parser {
    public static final String COMMAND_DONE = "done";
    public static final int TASK_DATE_INDEX = 1;
    private static final int TODO_START_INDEX = 4;
    private static final int EVENT_START_INDEX = 5;
    private static final int DEADLINE_START_INDEX = 8;
    private static final int DONE_NUMBER_INDEX = 4;
    private static final int DELETE_NUMBER_INDEX = 6;
    private static final int FIND_START_INDEX = 4;
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_LIST_SHORT = "l";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_HELP_SHORT = "h";
    private static final String COMMAND_FIND = "find";
    private static final Scanner in = new Scanner(System.in);
    private static String userInput;

    private static void setUserInput() {
        userInput = in.nextLine().trim();
    }


    public static void parseAndExecuteCommand() {
        boolean isExit = false;
        do {
            setUserInput();
            Ui.printDivider();

            if (userInput.equals(COMMAND_LIST_SHORT) || userInput.equals(COMMAND_LIST)) {
                Duke.taskList.listTasks();

            } else if (userInput.startsWith(COMMAND_TODO)) {
                Duke.taskList.addTaskPlusException(CommandEnum.TODO, userInput);

            } else if (userInput.startsWith(COMMAND_DEADLINE)) {
                Duke.taskList.addTaskPlusException(CommandEnum.DEADLINE, userInput);

            } else if (userInput.startsWith(COMMAND_EVENT)) {
                Duke.taskList.addTaskPlusException(CommandEnum.EVENT, userInput);

            } else if (userInput.startsWith(COMMAND_DONE)) {
                Duke.taskList.doneOrDeleteTaskPlusException(userInput, COMMAND_DONE);

            } else if (userInput.startsWith(COMMAND_DELETE)) {
                Duke.taskList.doneOrDeleteTaskPlusException(userInput, COMMAND_DELETE);

            } else if (userInput.startsWith(COMMAND_FIND)) {
                Duke.taskList.findTasksPlusException(userInput);

            } else if (userInput.equals(COMMAND_BYE)) {
                Ui.printlnTab("Bye. Hope to see you again soon!");
                Ui.printDivider();
                isExit = true;

            } else if (userInput.equals("")) { //empty command
                Ui.printlnTab("Please enter a command.");
                Ui.printDivider();

            } else if (userInput.equals(COMMAND_HELP_SHORT) || userInput.equals(COMMAND_HELP)) { //empty command
                Ui.printlnTab("List of commands:");
                Ui.printlnTab("1. l OR list ");
                Ui.printlnTab("2. todo [TASK DESCRIPTION]");
                Ui.printlnTab("3. deadline [TASK DESCRIPTION] /by [DEADLINE]");
                Ui.printlnTab("4. event [TASK DESCRIPTION] /at [DATE/TIME]");
                Ui.printlnTab("5. delete [TASK NUMBER]");
                Ui.printlnTab("6. done [TASK NUMBER]");
                Ui.printlnTab("7. find [PART OF TASK DESCRIPTION]");
                Ui.printlnTab("8. h OR help");
                Ui.printlnTab("9. bye");
                Ui.printDivider();

            } else { //Invalid inputs
                Ui.printlnTab("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
                Ui.printDivider();
            }

        } while (!isExit);

    }


    //command keyword removed eg. "todo clean room"  -> "clean room"
    public static String stripCommandWord(CommandEnum commandType, String userInput) throws BlankDescriptionException {
        String strippedUserInput = "";

        switch (commandType) {
        case TODO:
            strippedUserInput = userInput.substring(TODO_START_INDEX).strip(); // remove "todo" from userInput
            break;
        case DEADLINE:
            strippedUserInput = userInput.substring(DEADLINE_START_INDEX).strip(); // remove "deadline" from userInput
            break;
        case EVENT:
            strippedUserInput = userInput.substring(EVENT_START_INDEX).strip(); // remove event
            break;
        case DONE:
            strippedUserInput = userInput.substring(DONE_NUMBER_INDEX).strip();
            break;
        case DELETE:
            strippedUserInput = userInput.substring(DELETE_NUMBER_INDEX).strip();
            break;
        case FIND:
            strippedUserInput = userInput.substring(FIND_START_INDEX).strip();
            break;
        }

        if (strippedUserInput.isBlank()) {
            throw new BlankDescriptionException();
        }
        return strippedUserInput;
    }

    public static String[] getTaskDetails(CommandEnum taskType, String userInputWithoutTaskCommand) throws IncompleteInformationException {
        String[] taskDetails;
        //strip userInputWithoutTaskCommand prevents empty description / dates
        if (taskType == CommandEnum.DEADLINE) {
            taskDetails = userInputWithoutTaskCommand.strip().split("/by");
        } else { //if EVENT
            taskDetails = userInputWithoutTaskCommand.strip().split("/at");
        }

        // taskDetails[] should have length of 2
        //containing Task description (index 0) and Task date (index 1)
        //special case of length 2 when "/by timing" which is still invalid
        //is checked by .isBlank()
        if (taskDetails.length != 2
                || taskDetails[TaskList.TASK_DESCRIPTION_INDEX].isBlank()
                || taskDetails[TASK_DATE_INDEX].isBlank()) {
            throw new IncompleteInformationException();
        }

        for (int i = 0; i < 2; i++) {
            taskDetails[i] = taskDetails[i].strip();
        }
        return taskDetails;
    }

    static void parseStorageData(String line) throws InvalidIntegerException {
        if (line.isBlank()) {
            return;
        }
        String[] taskDetails = line.split(" \\| ");

        String taskLetter = taskDetails[0];

        String isDoneString = taskDetails[1];
        int isDoneInt = Integer.parseInt(isDoneString);
        if (isDoneInt != Integer.parseInt("1") && isDoneInt != Integer.parseInt("0")) {
            throw new InvalidIntegerException();
        }
        boolean isDone = (isDoneInt == 1);

        String description = taskDetails[2];
        String date;

        switch (taskLetter) {
        case "T":
            Duke.taskList.addTodo(description, isDone);
            break;

        case "D":
            date = taskDetails[3];
            String[] deadlineDetails = {description, date};

            Duke.taskList.addDeadline(deadlineDetails, isDone);
            break;

        case "E":
            date = taskDetails[3];
            String[] eventDetails = {description, date};
            Duke.taskList.addEvent(eventDetails, isDone);
            break;
        default:
            throw new IllegalStateException();
        }
    }
}
