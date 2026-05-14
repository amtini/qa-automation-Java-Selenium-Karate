package e2e.pages;

import e2e.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutStepTwoPage extends BasePage {

    private static final By CART_ITEMS         = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME          = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESC          = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICE         = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SUBTOTAL_LABEL     = By.cssSelector("[data-test='subtotal-label']");
    private static final By PAYMENT_INFO_VALUE = By.cssSelector("[data-test='payment-info-value']");
    private static final By SHIPPING_INFO_VALUE = By.cssSelector("[data-test='shipping-info-value']");
    private static final By FINISH_BUTTON      = By.cssSelector("[data-test='finish']");

    private final HeaderComponent header;

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public List<Product> items() {
        waitForVisible(CART_ITEMS);
        return driver.findElements(CART_ITEMS).stream()
                .map(this::toProduct)
                .toList();
    }

    public BigDecimal itemSubtotal() {
        return parseLabelPrice(textOf(SUBTOTAL_LABEL));
    }

    public String paymentInfo() {
        return textOf(PAYMENT_INFO_VALUE);
    }

    public String shippingInfo() {
        return textOf(SHIPPING_INFO_VALUE);
    }

    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }

    private Product toProduct(WebElement row) {
        return new Product(
                row.findElement(ITEM_NAME).getText(),
                parsePrice(row.findElement(ITEM_PRICE).getText()),
                row.findElement(ITEM_DESC).getText()
        );
    }

    private static BigDecimal parsePrice(String text) {
        return new BigDecimal(text.replace("$", "").trim());
    }

    private static BigDecimal parseLabelPrice(String label) {
        int dollarIndex = label.indexOf('$');
        return new BigDecimal(label.substring(dollarIndex + 1).trim());
    }
}
