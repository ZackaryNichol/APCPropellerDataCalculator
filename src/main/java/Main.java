import dataOutput.CalcOutput;
import dataParsing.PropellerDataLoader;

/**
 * Initializes and starts this application
 */
public class Main {

    //The max voltage the driving motor can output
    //TODO: Implement voltage constraint
    private static final double MAX_VOLTAGE = 22.2;
    private static final double MINIMUM_TORQUE = 1.0;
    private static final double MINIMUM_THRUST = 9.0;

    /**
     * Gathers all resources, then starts sending data to the webDriver.
     */
    private void run() {
        PropellerDataLoader dataSets = new PropellerDataLoader("src/main/resources/propellerData");
        CalcOutput.outputFilteredDataSets(dataSets, MAX_VOLTAGE, MINIMUM_TORQUE, MINIMUM_THRUST);
    }

    /**
     * Gives an entry point to starting the application
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        new Main().run();
    }
}