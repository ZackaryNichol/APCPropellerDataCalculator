package dataOutput;

import com.opencsv.CSVWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A util class to output a structured file
 */
public class CalcOutput {

    private static final String OUTPUT_FILE_NAME = "PropMotorMatch";

    private static boolean fileInitialized = false;

    private static File outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + ".csv");

    private static void initOutputFile() {
        int counter = 1;
        while (outputFile.exists()) {
            outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + counter + ".csv");
            counter++;
        }

        fileInitialized = true;
    }

    /** TODO: Implement
     *
     * @param lineToWrite
     * @param propRPM
     * @param collectedData
     */
    @Contract(pure = true)
    public static void writeCalcOutput(String lineToWrite, int propRPM, @Nullable String[][] collectedData) {

        if (!fileInitialized) {
            initOutputFile();
        }

        try {
            FileWriter outputWriter = new FileWriter(outputFile, true);
            CSVWriter writer = new CSVWriter(outputWriter);

            String[] columnLabels = { lineToWrite, String.valueOf(propRPM), "MotorList" };
            writer.writeNext(columnLabels);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
