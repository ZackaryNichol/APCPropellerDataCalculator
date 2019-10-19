package dataParsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * A loader that loads all propeller data into an ArrayList< PropellerDataOrganizer >
 */
public class PropellerDataLoader extends DataSetLoader {

    //The list that contains all propeller data of all propellers
    private ArrayList<PropellerDataOrganizer> allPropellerData = new ArrayList<>();

    /**
     * Calls for loading, then parsing of propeller data files
     * @param dataPath The root path of propeller data files
     */
    public PropellerDataLoader(String dataPath) {
        loadDataFiles(dataPath);
    }

    /**
     * Parses the given data file into its own PropellerDataOrganizer
     * @param fileToParse The given propeller data file to parse
     */
    protected void parseDataFile(FileReader fileToParse) {
        BufferedReader br = new BufferedReader(fileToParse);
        try {
            String currentLine = br.readLine();
            String propName = currentLine.substring(currentLine.indexOf(" (") + 2, currentLine.lastIndexOf("."));

            //Parsed data store
            ArrayList<String[]> dataTable = new ArrayList<>();

            //Parses each line of file into usable data
            while ((currentLine = br.readLine()) != null) {
                String[] parsedLine = parseLine(currentLine);
                if (parsedLine != null) {
                    dataTable.add(parsedLine);
                }
            }
            allPropellerData.add(new PropellerDataOrganizer(propName, dataTable));
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Parses the given line into usable data, which is either an RPM number, or a list of propeller data values
     * @param lineToParse The given line to parse
     * @return Each element sequentially, parsed
     */
    @Nullable
    private String[] parseLine(@NotNull String lineToParse) {
        //Replace characters to allow for easier regex
        lineToParse = lineToParse.replace("-", " -");
        lineToParse = lineToParse.replace("-NaN", " ");
        lineToParse = lineToParse.trim().replaceAll(" +", " ");

        //Splits between every number
        String[] lineSplit = lineToParse.split("[^0-9.,-]");
        for (String s : lineSplit) {

            //Filter garbage data
            if (!s.isEmpty() && (s.length() > 2)) {

                //RPM is always whole number, not a float (RPM has no '.')
                if (!s.contains(".")) {
                    String[] rpm = new String[1];
                    rpm[0] = s.trim();
                    return rpm;
                } else {
                    return lineSplit;
                }
            }
        }

        //Returns null only is data file is incorrectly formatted
        return null;
    }

    /**
     * @return The number of propeller data files loaded
     */
    public int getNumOfDiffProps() {
        return allPropellerData.size();
    }

    /**
     * @param propIndex The propeller data file index to find RPM values of
     * @return All RPM values of the given propeller data file (by index proxy)
     */
    public Set<Integer> getPropRPMs(int propIndex) {
        return allPropellerData.get(propIndex).getMappedData().keySet();
    }

    /**
     * @param propIndex The propeller data file index to find the name of
     * @return The name of the given propeller data file (by index proxy)
     */
    public String getPropTableName(int propIndex) {
        return allPropellerData.get(propIndex).getName();
    }

    /**
     * @param propIndex The propeller data file index
     * @param propRPM The RPM to match with RPM data table
     * @return The number of rows of data for a given (by index proxy) RPM data table
     */
    public int getPropDataNumOfRows(int propIndex, int propRPM) {
        return allPropellerData.get(propIndex).getMappedData().get(propRPM).length;
    }

    /**
     * @param propIndex The propeller data file index
     * @param propRPM The RPM to match with RPM data table
     * @param propRPMTableRowNum The line number of a given (by index proxy) RPM data table
     * @return A torque value from a specific given line (by index proxy) of the given (by index proxy) RPM data table
     */
    public double getTorqueValue(int propIndex, int propRPM, int propRPMTableRowNum) {
        /*System.out.print(allPropellerData.get(propIndex).getName() + " " + propRPM + " ");
        System.out.print(allPropellerData.get(propIndex).getMappedData().get(propRPM)[propRPMTableRowNum][6]);
        System.out.println(" ");*/
        return allPropellerData.get(propIndex).getMappedData().get(propRPM)[propRPMTableRowNum][6] * 12;
    }
}