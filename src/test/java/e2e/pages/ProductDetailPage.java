package e2e.pages;

import e2e.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

public class ProductDetailPage extends BasePage {

    private static final By ITEM_NAME          = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESC          = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICE         = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector("[data-test^='add-to-cart']");
    private static final By BACK_TO_PRODUCTS   = By.cssSelector("[data-test='back-to-products']");

    private final HeaderComponent header;

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public Product captureProduct() {
        return new Product(
                textOf(ITEM_NAME),
                parsePrice(textOf(ITEM_PRICE)),
                textOf(ITEM_DESC)
        );
    }

    public ProductDetailPage addToCart() {
        click(ADD_TO_CART_BUTTON);
        return this;
    }

    public InventoryPage backToProducts() {
        click(BACK_TO_PRODUCTS);
        return new InventoryPage(driver);
    }

    private static BigDecimal parsePrice(String text) {
        return new BigDecimal(text.replace("$", "").trim());
    }
}
