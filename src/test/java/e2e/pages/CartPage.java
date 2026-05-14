package e2e.pages;

import e2e.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESC = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    private final HeaderComponent header;

    public CartPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public List<Product> items() {
        waitForVisible(CART_ITEMS);
        return driver.findElements(CART_ITEMS).stream().map(this::toProduct).toList();
    }

    public BigDecimal calculateItemTotal() {
        return items().stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CheckoutStepOnePage clickCheckout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutStepOnePage(driver);
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
}
