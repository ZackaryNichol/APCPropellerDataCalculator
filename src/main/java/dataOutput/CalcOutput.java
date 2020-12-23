package dataOutput;

import com.opencsv.CSVWriter;
import dataParsing.PropellerDataLoader;
import dataParsing.PropellerDataSet;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static dataParsing.PropellerDataLoader.POWER_CONSTANT;

/**
 * Outputs a structured csv file using the given data set and constraints
 */
public class CalcOutput {

    //The number of dynamic thrust data points to collect for each propeller
    public static final int NUM_DATA_POINTS = 85;

    //The output name of the file that propeller data will be written to
    private static final String OUTPUT_FILE_NAME = "UsefulPropellerData";

    //If this file output has been initialized or not
    private static boolean fileInitialized = false;

    //The output file that all propeller data is written to
    private static File outputFile = new File("src/CalcOutput/" + OUTPUT_FILE_NAME + ".csv");

    /**
     * Writes each propeller data set's dynamic thrust data to the output file
     * @param allPropData The propeller data set to write
     */
    public static void writeDynamicThrustData(PropellerDataLoader allPropData) {
        //Loops through every line of every RPM data of every propeller file
        for (int i = 0; i < allPropData.getNumOfProps(); i++) {

            PropellerDataSet propData = allPropData.getPropellerDataAt(i);
            String propName = propData.getName();
            System.out.println("Writing prop: " + propName);

            ArrayList<Integer> propRPMS = new ArrayList<>(propData.getPropRPMs());

            //Write static thrust
            writeCalcOutput(
                propName, 0, 0, POWER_CONSTANT, propData.getStaticThrust(),
                propData.getDynamicThrustPrediction(0), ""
            );


            //Generates ~100-200 dynamic thrust numbers for each rpm
            int velocityCounter = 1;

            outerLoop:
            for (int j = 0; j < propRPMS.size() - 1; j++) {
                for (int k = 0; k < 200; k++) {

                    double interRPM = propData.InterpolateRPM(velocityCounter, propRPMS.get(j), propRPMS.get(j + 1));
                    double interThrust = propData.getDynamicThrust(velocityCounter, propRPMS.get(j), propRPMS.get(j + 1));
                    double predictedThrust = propData.getDynamicThrustPrediction(velocityCounter);

                    if (interThrust > 0 && velocityCounter < NUM_DATA_POINTS) {
                            writeCalcOutput(
                                    propName, velocityCounter, interRPM, POWER_CONSTANT, interThrust, predictedThrust, ""
                            );
                            if (velocityCounter == NUM_DATA_POINTS - 1) {
                                velocityCounter++;
                                writeCalcOutput(
                                        propName, velocityCounter, interRPM, POWER_CONSTANT, interThrust, predictedThrust,
                                        propData.getThrustFormula()
                                );
                                break outerLoop;
                            }
                        velocityCounter++;
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

            String[] columnLabels = {
                "PropName", "Velocity (mph)", "RPM", "Power (hp)", "Thrust (Lbf)",
                "Predicted Thrust", "Thrust Formula"
            };
            writer.writeNext(columnLabels);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the output file the given data in a structured csv format
     * @param propName The name of the prop to write
     * @param velocity The velocity value to write
     * @param RPM The RPM value to write out
     * @param power The power value to write
     * @param thrust The thrust value to write
     * @param prediction The thrust prediction value to write
     */
    @Contract(pure = true)
    private static void writeCalcOutput(
            String propName, double velocity, double RPM, double power,
            double thrust, double prediction, String thrustFormula) {

        if (!fileInitialized) {
            initOutputFile();
        }

        try {
            FileWriter outputWriter = new FileWriter(outputFile, true);
            CSVWriter writer = new CSVWriter(outputWriter);

            String[] values = {
                propName, String.valueOf(velocity), String.valueOf(RPM), String.valueOf(power),
                String.valueOf(thrust), String.valueOf(prediction), thrustFormula
            };
            writer.writeNext(values);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}