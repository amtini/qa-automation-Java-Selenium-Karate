package e2e.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HeaderComponent extends BasePage {

    private static final By CART_QTY = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public int cartItemCount() {
        List<WebElement> cartQtyElement = driver.findElements(CART_QTY);
        if(cartQtyElement.isEmpty()){ return 0; }
        return Integer.parseInt(cartQtyElement.get(0).getText());
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }
}
