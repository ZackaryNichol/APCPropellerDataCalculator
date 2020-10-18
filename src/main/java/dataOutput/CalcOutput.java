package dataOutput;

import com.opencsv.CSVWriter;
import dataParsing.PropellerDataLoader;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Outputs a structured csv file using the given data set and constraints
 */
public class CalcOutput {

    //The output name of the file that propeller data will be written to
    private static final String OUTPUT_FILE_NAME = "UsefulPropellerData";

    //If this file output has been initialized or not
    private static boolean fileInitialized = false;

    //The output file that all propeller data is written to
    private static File outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + ".csv");

    /**
     * Writes each propeller data set's data to this output file given that the propeller data fits the given
     * constraints
     * @param dataSet The propeller data set to write if it passes the given constraints.
     * @param maxVoltage TODO: Implement
     */
    public static void outputFilteredDataSets(
        PropellerDataLoader dataSet, double maxVoltage, double minimumTorque, double minimumThrust
    ) {
        //Loops through every line of every RPM data of every propeller file
        for (int i = 0; i < dataSet.getNumOfProps(); i++) {
            String propName = dataSet.getPropTableName(i);
            System.out.println("Writing prop: " + propName);

            for (int propRPM : dataSet.getPropRPMs(i)) {

                for (int q = 0; q < dataSet.getPropDataNumOfRows(i, propRPM); q++) {
                    double torqueValue = dataSet.getTorqueValue(i, propRPM, q);
                    double thrustValue = dataSet.getThrustValue(i, propRPM, q);

                    if (torqueValue > minimumTorque && thrustValue > minimumThrust) {
                        writeCalcOutput(propName, thrustValue, torqueValue, propRPM);
                    }
                }

            }
        }
    }

    /**
     * Creates an output csv file and writes the header.
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

            String[] columnLabels = { "PropName", "Thrust (Lbf)", "Torque(lbf-ft)", "RPM" };
            writer.writeNext(columnLabels);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given propeller data elements to this output file.
     * @param propName The name of the prop to write
     * @param propTorque The torque of the propName prop
     * @param propRPM An RPM from the propName prop to write
     */
    @Contract(pure = true)
    private static void writeCalcOutput(String propName, double propTorque, double propThrust, int propRPM) {

        if (!fileInitialized) {
            initOutputFile();
        }

        try {
            FileWriter outputWriter = new FileWriter(outputFile, true);
            CSVWriter writer = new CSVWriter(outputWriter);

            String[] values = { propName, String.valueOf(propTorque), String.valueOf(propThrust), String.valueOf(propRPM) };
            writer.writeNext(values);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}