package e2e.pages;

import e2e.data.CheckoutFormData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutStepOnePage extends BasePage {

    private static final By FIRST_NAME       = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME        = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE      = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE_BUTTON  = By.cssSelector("[data-test='continue']");

    private final HeaderComponent header;

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public CheckoutStepTwoPage fillForm(CheckoutFormData data) {
        type(FIRST_NAME, data.firstName());
        type(LAST_NAME, data.lastName());
        type(POSTAL_CODE, data.postalCode());
        click(CONTINUE_BUTTON);
        return new CheckoutStepTwoPage(driver);
    }
}
