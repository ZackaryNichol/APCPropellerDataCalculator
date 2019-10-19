package dataParsing;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Organization a propeller data file into a LinkedHashMap(Integer (RPM), double[][] (actual data)) structure
 */
class PropellerDataOrganizer extends DataSetOrganizer {

    private String name;

    //The main datastructure that holds all the propeller data for this propeller
    private LinkedHashMap<Integer, double[][]> mappedData;

    /**
     * Organizes propeller data upon object creation
     * @param propName The name of the propeller associated with this object
     * @param rawData The parsed (cleaned) unorganized data from the data-set loader
     */
    PropellerDataOrganizer(String propName, ArrayList<String[]> rawData) {
        System.out.print("Created organizer for " + propName + ", organizing data ...");
        this.name = propName;
        mappedData = new LinkedHashMap<>();
        organizeData(rawData);
        System.out.print("Data organized! \n");
    }

    /**
     * Organizes rawData into the mappedData structure.
     * @param rawData The given rawData
     */
    protected void organizeData(@NotNull ArrayList<String[]> rawData) {
        int tableEndIndex = 1;
        int tableStartIndex = 1;

        for (int i = 0; i < rawData.size(); i++) {
            if ((rawData.get(i).length == 1 && i != 0) || i == rawData.size() - 1) {
                tableEndIndex = i;
                mappedData.put(Integer.parseInt(rawData.get(tableStartIndex - 1)[0]), createDataArray(rawData, tableStartIndex, tableEndIndex));
                tableStartIndex = i + 1;
            }
            else {
                tableEndIndex++;
            }
        }
        //System.out.println(name);
        //mappedData.forEach((key, value) -> System.out.println(key + " " + Arrays.deepToString(value)));
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
        double[][] dataArray = new double[tableEndIndex - tableStartIndex][rawData.get(1).length];
        for (int i = tableStartIndex; i < tableEndIndex; i++) {
            if (rawData.get(i).length == 1) {
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