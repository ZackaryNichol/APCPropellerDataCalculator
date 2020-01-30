package dataParsing;

import java.util.ArrayList;

/**
 * A template for needed elements to organize a data file
 */
abstract class DataSetOrganizer {

    /**
     * Organizes the given data into a desirable format
     * @param rawData The unorganized given data
     */
    protected abstract void organizeData(ArrayList<String[]> rawData);
}
