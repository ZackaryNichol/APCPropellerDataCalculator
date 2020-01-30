package dataOutput;

import com.opencsv.CSVWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Outputs a structured csv file
 */
public class CalcOutput {

    private static final String OUTPUT_FILE_NAME = "UsefulPropellerData";

    private static boolean fileInitialized = false;

    private static File outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + ".csv");

    /**
     * Creates an output csv file.
     */
    private static void initOutputFile() {
        int counter = 1;
        while (outputFile.exists()) {
            outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + counter + ".csv");
            counter++;
        }

        fileInitialized = true;

        try {
            FileWriter outputWriter = new FileWriter(outputFile, true);
            CSVWriter writer = new CSVWriter(outputWriter);

            String[] columnLabels = { "PropName", "Torque(lbf-ft)", "RPM"};
            writer.writeNext(columnLabels);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes changes to this output file.
     * @param propName The name of the prop that calcs were done with
     * @param propTorque The torque of the propName prop
     * @param propRPM The RPM for the propName prop
     */
    @Contract(pure = true)
    public static void writeCalcOutput(String propName, double propTorque, int propRPM) {

        if (!fileInitialized) {
            initOutputFile();
        }

        try {
            FileWriter outputWriter = new FileWriter(outputFile, true);
            CSVWriter writer = new CSVWriter(outputWriter);

            String[] values = { propName, String.valueOf(propTorque), String.valueOf(propRPM) };
            writer.writeNext(values);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}