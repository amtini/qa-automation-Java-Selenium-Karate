package e2e.pages;

import e2e.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;

public class InventoryPage extends BasePage {

    private static final By PRODUCT_CARDS = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME     = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_DESC     = By.cssSelector("[data-test='inventory-item-desc']");
    private static final By ITEM_PRICE    = By.cssSelector("[data-test='inventory-item-price']");

    private final HeaderComponent header;

    public InventoryPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    public Product captureProduct(String name) {
        WebElement card = findProductCard(name);
        return toProduct(card);
    }

    public InventoryPage addToCart(String name) {
        click(addToCartLocator(name));
        return this;
    }

    public ProductDetailPage openProductDetail(String name) {
        WebElement card = findProductCard(name);
        card.findElement(ITEM_NAME).click();
        return new ProductDetailPage(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    private WebElement findProductCard(String name) {
        waitForVisible(PRODUCT_CARDS);
        return driver.findElements(PRODUCT_CARDS).stream()
                .filter(card -> card.findElement(ITEM_NAME).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Product not found on inventory page: " + name));
    }

    private Product toProduct(WebElement card) {
        return new Product(
                card.findElement(ITEM_NAME).getText(),
                parsePrice(card.findElement(ITEM_PRICE).getText()),
                card.findElement(ITEM_DESC).getText()
        );
    }

    private static By addToCartLocator(String productName) {
        String slug = productName.toLowerCase().replace(" ", "-");
        return By.cssSelector("[data-test='add-to-cart-" + slug + "']");
    }

    private static BigDecimal parsePrice(String text) {
        return new BigDecimal(text.replace("$", "").trim());
    }
}
