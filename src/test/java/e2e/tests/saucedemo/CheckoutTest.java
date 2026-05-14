package e2e.tests.saucedemo;

import e2e.config.Config;
import e2e.data.CheckoutFormData;
import e2e.data.Product;
import e2e.factories.DataFactory;
import e2e.factories.DriverFactory;
import e2e.hooks.DriverManager;
import e2e.hooks.TestListener;
import e2e.pages.CartPage;
import e2e.pages.CheckoutCompletePage;
import e2e.pages.CheckoutStepTwoPage;
import e2e.pages.InventoryPage;
import e2e.pages.LoginPage;
import e2e.pages.ProductDetailPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.List;

import static e2e.data.Products.BACKPACK;
import static e2e.data.Products.BIKE_LIGHT;
import static e2e.data.SauceDemoConstants.CONFIRMATION_TEXT;
import static e2e.data.SauceDemoConstants.EXPECTED_PAYMENT_INFO;
import static e2e.data.SauceDemoConstants.EXPECTED_SHIPPING_INFO;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TestListener.class)
public class CheckoutTest {

    private static final String USER = "standard_user";
    private static final String PASS = "secret_sauce";

    @BeforeEach
    public void setUp() {
        WebDriver driver = DriverFactory.create(Config.getBrowser(), Config.isHeadless());
        DriverManager.set(driver);
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quit();
    }

    @Test
    public void completeHappyPathCheckout() {
        WebDriver driver = DriverManager.get();

        // Login and land on inventory
        InventoryPage inventory = new LoginPage(driver)
                .open()
                .loginAs(USER, PASS);

        // Capture and add the first product from the inventory list
        Product backpack = inventory.captureProduct(BACKPACK);
        inventory.addToCart(BACKPACK);

        // Open the detail page of the second product, capture and add it from there
        ProductDetailPage detail = inventory.openProductDetail(BIKE_LIGHT);
        Product bikeLight = detail.captureProduct();
        detail.addToCart();

        // Cart badge reflects both products
        assertThat(detail.header().cartItemCount()).isEqualTo(2);

        // Open cart and validate contents and subtotal against what we captured
        List<Product> capturedProducts = List.of(backpack, bikeLight);
        BigDecimal expectedSubtotal = capturedProducts.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartPage cart = detail.header().openCart();
        assertThat(cart.items()).containsExactlyInAnyOrderElementsOf(capturedProducts);
        assertThat(cart.calculateItemTotal()).isEqualByComparingTo(expectedSubtotal);

        // Proceed to checkout and fill the form with generated data
        CheckoutFormData formData = DataFactory.formData();
        CheckoutStepTwoPage step2 = cart.clickCheckout().fillForm(formData);

        // Step 2 reflects the same items, same subtotal, and SauceDemo's static values
        assertThat(step2.items()).containsExactlyInAnyOrderElementsOf(capturedProducts);
        assertThat(step2.itemSubtotal()).isEqualByComparingTo(expectedSubtotal);
        assertThat(step2.paymentInfo()).isEqualTo(EXPECTED_PAYMENT_INFO);
        assertThat(step2.shippingInfo()).isEqualTo(EXPECTED_SHIPPING_INFO);

        // Finish and assert confirmation
        CheckoutCompletePage complete = step2.finish();
        assertThat(complete.confirmationMessage()).containsIgnoringCase(CONFIRMATION_TEXT);
    }
}
