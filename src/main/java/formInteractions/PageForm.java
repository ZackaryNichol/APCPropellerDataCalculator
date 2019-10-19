package formInteractions;

import org.jetbrains.annotations.Contract;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * A utility class to fill form elements
 */
public class PageForm {

    private WebDriver pageDriver;

    /**
     * Creates a new PageForm which provides an interface to the WebDriver
     * @param pageDriver The WebDriver for the given page
     */
    @Contract(pure = true)
    public PageForm(WebDriver pageDriver) {
        this.pageDriver = pageDriver;
    }

    /**
     * Fills a field with the given double's value
     * @param elementId The id to find the element by
     * @param fieldValue The value to fill the element with
     */
    void fillField(String elementId, double fieldValue) {
        WebElement element = createElementFromId(elementId);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(fieldValue));
    }

    /**
     * Fills a field with the given int's value
     * @param elementId The id to find the element by
     * @param fieldValue The value to fill the element with
     */
    void fillField(String elementId, int fieldValue) {
        WebElement element = createElementFromId(elementId);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(fieldValue));
    }

    /**
     * Clicks the element whose id matches the given id
     * @param buttonId The id to match to an element
     */
    void clickButton(String buttonId) {
        createElementFromId(buttonId).click();
    }

    /**
     * Selects a dropdown whose element matches dropdownId and whose option text matches dropdownOption
     * @param dropdownId The id to find the dropdown by
     * @param dropdownOption The text to find the dropdown option by
     */
    void selectDropdownValue(String dropdownId, String dropdownOption) {
        Select dropdown = new Select(createElementFromId(dropdownId));
        dropdown.selectByVisibleText(dropdownOption);
    }

    /**
     * Creates a new WebElement whose id matches the one given
     * @param id The id to find the WebElement by
     * @return The found WebElement
     */
    private WebElement createElementFromId(String id) {
        return pageDriver.findElement(By.id(id));
    }
}
