package formInteractions;

import dataParsing.DataSetLoader;
import dataParsing.PropellerDataLoader;

/**
 * A utility class to easily call form fill functions sequentially.
 */
public abstract class PageFormFill {

    /**
     * Fills the given form with the given data in a way dependant on the id of the page.
     * @param pageId The arbitrary id of the webpage
     * @param form The form to fill
     * @param dataSet The data to fill the form with
     */
    public static void fillPage(int pageId, PageForm form, DataSetLoader dataSet) {

        if (pageId == 0) {
            new ECalcFormFill().runFormInput((PropellerDataLoader) dataSet);
        }
    }

    /**
     * Fills the given form with the given data
     * @param dataSet The data to fill the form with
     */
    abstract void runFormInput(DataSetLoader dataSet);

}