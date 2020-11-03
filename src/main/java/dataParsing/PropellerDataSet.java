package dataParsing;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Organizes a propeller data file into a LinkedHashMap(Integer (RPM), double[][] (actual data)) structure
 */
class PropellerDataSet {

    private final String name;

    //The main data structure that holds all the propeller data for this propeller
    private final LinkedHashMap<Integer, double[][]> mappedData;

    /**
     * Organizes propeller data upon object creation
     * @param propName The name of the propeller associated with this object
     * @param rawData The parsed (cleaned) unorganized data from the data-set loader
     */
    PropellerDataSet(String propName, ArrayList<String[]> rawData) {
        System.out.print("Created data set for " + propName + ", organizing data ...");
        this.name = propName;
        this.mappedData = new LinkedHashMap<>();
        organizeData(rawData);
        System.out.print("Data organized! \n");
    }

    /**
     * Organizes rawData into the mappedData structure.
     * @param rawData The given rawData
     */
    protected void organizeData(@NotNull ArrayList<String[]> rawData) {
        int tableStartIndex = 1;
        int tableEndIndex = 1;

        for (int i = 0; i < rawData.size(); i++) {
            if (rawData.get(i).length == 1) {
                tableEndIndex = i;
                if (tableEndIndex - tableStartIndex > 0) {
                    mappedData.put(
                        Integer.parseInt(rawData.get(tableStartIndex - 1)[0]),
                        createDataArray(rawData, tableStartIndex, tableEndIndex)
                    );
                }
                tableStartIndex = i + 1;
            }
            else {
                tableEndIndex++;
            }
        }
    }

    /**
     * Creates a 2D array from an entire (specified) RPM table (excluding the RPM value itself)
     * @param rawData All of the rawData
     * @param tableStartIndex (Inclusive) The start of the relevant RPM table data
     * @param tableEndIndex (Inclusive) The end of the relevant RPM table data
     * @return The populated 2D array containing exactly one complete RPM table
     */
    @NotNull
    private double[][] createDataArray(@NotNull ArrayList<String[]> rawData, int tableStartIndex, int tableEndIndex) {
        if (rawData.get(1).length <= 1) {
            tableStartIndex++;
        }

        double[][] dataArray = new double[tableEndIndex - tableStartIndex][rawData.get(tableStartIndex).length];
        for (int i = tableStartIndex; i < tableEndIndex; i++) {
            if (rawData.get(i).length <= 1) {
                System.out.println("Next set ...");
                break;
            }
            else {
                for (int k = 0; k < rawData.get(i).length; k++) {
                    if (!rawData.get(i)[k].equals("-")) {
                        dataArray[i - tableStartIndex][k] = Double.parseDouble(rawData.get(i)[k]);
                    }
                }
            }
        }
        return dataArray;
    }

    /**
     * @return The propeller name associated with this data-set organizer
     */
    String getName() {
        return name;
    }

    /**
     * @return The mapped RPM tables associated with this data-set organizer's associated propeller
     */
    HashMap<Integer, double[][]> getMappedData() {
        return mappedData;
    }
}