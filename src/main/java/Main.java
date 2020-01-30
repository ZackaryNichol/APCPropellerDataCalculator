import dataParsing.PropellerDataLoader;
import formInteractions.PageForm;
import formInteractions.PageFormFill;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * A class to initialize and start the application.
 */
public class Main {

    /**
     * Connects to the host's webDriver, then opens the configured page. Requires a webDriver to be running on the host's
     * machine.
     * @return The configured webDriver
     */
    @NotNull
    private static WebDriver initWebpage() {
        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL("http://localhost:9515"), new ChromeOptions());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(driver).get("https://www.ecalc.ch/torquecalc.php");
        return driver;
    }

    /**
     * Gathers all resources, then starts sending data to the webDriver.
     */
    private void run() {
        PropellerDataLoader dataSet = new PropellerDataLoader("src/main/resources/propellerData");
        //WebDriver pageDriver = initWebpage();
        PageFormFill.fillPage(0, null, dataSet);
    }

    /**
     * Gives an entry point to starting the application
     * @param args unused command line arguments
     */
    public static void main(String[] args) {
        new Main().run();
    }
}