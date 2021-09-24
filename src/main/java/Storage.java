import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Executes all operations related to reading and writing the DukeData/data.txt
 */
public class Storage {
    public static final Path dataPath = Paths.get("DukeData/data.txt");
    private static final Path dukeDirPath = Paths.get("DukeData");
    private static final File dukeDir = new File(dukeDirPath.toString());
    private static final File dataFile = new File(dataPath.toString());

    /**
     * Creates DukeData directory and/or DukeData/data.txt if they do not exist respectively
     * Then reads DukeData/data.txt
     * Prints if these operations are done, or if error found
     *
     * @throws IOException if any of the file operations fail
     */
    public static void initializeStorage() throws IOException {
        if (dukeDir.mkdir()) {
            Ui.printlnTab("Created " + dukeDirPath + " directory!");
        }
        if (dataFile.createNewFile()) {
            Ui.printlnTab("Created " + dataPath + " storage file!");
        }

        try {
            readFile();
        } catch (FileNotFoundException e) {
            Ui.printlnTab(dataPath + " not found!");
            Ui.printDivider();
        }
    }

    /**
     * Reads in DukeData/data.txt file, parses each line in the file,
     * printing out if there are formatting errors in particular line and its line number
     *
     * @throws FileNotFoundException if DukeData/data.txt not found
     */
    private static void readFile() throws FileNotFoundException {
        Ui.printlnTab("Reading " + dataPath + " file...");
        Ui.printlnTab("");

        boolean hasError = false;
        Scanner s = new Scanner(dataFile);
        int lineNumber = 1;
        while (s.hasNext()) {
            try {
                Parser.parseStorageData(s.nextLine());

            } catch (Exception e) {
                Ui.printlnTab("☹ OOPS!!! Line " + lineNumber + " is invalid! Skipping to next line...");
                hasError = true;
            }

            lineNumber++;
        }
        Ui.printlnTab("Done reading DukeData/data.txt");

        if (hasError) {
            Ui.printlnTab("");
            Ui.printlnTab("1. Enter 'bye' to exit program to correct data file " + dataPath);
            Ui.printlnTab("2. add, do or delete tasks to OVERWRITE all invalid data!");
        }
        Ui.printDivider();
    }

    //

    /**
     * Overwrite entire data.txt file with the latest version of tasks list
     *
     * @throws IOException if unable to write to DukeData/data.txt
     */
    public static void writeToFile() throws IOException {
        FileWriter fw = new FileWriter(dataFile);
        fw.write(Duke.taskList.getTasksDataStorageString());
        fw.close();
    }

}
