import dataOutput.CalcOutput;
import dataParsing.PropellerDataLoader;

/**
 * Initializes and starts this application
 */
public class Main {

    /**
     * Gathers all resources, then starts sending data to the webDriver.
     */
    private void run() {
        PropellerDataLoader dataSets = new PropellerDataLoader("src/main/resources/propellerData");
        CalcOutput.writeDynamicThrustData(dataSets);
    }

    /**
     * Gives an entry point to starting the application
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        new Main().run();
    }
}