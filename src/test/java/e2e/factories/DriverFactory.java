package e2e.factories;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.HashMap;
import java.util.Map;

public final class DriverFactory {

    private DriverFactory() {}

    public static WebDriver create(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> chrome(headless);
            case "firefox" -> firefox(headless);
            case "edge" -> edge(headless);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser + ". Use chrome, firefox or edge.");
        };
    }

    private static WebDriver chrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-notifications");
        options.addArguments("--window-size=1920,1080");
        options.setExperimentalOption("prefs", passwordManagerPrefs());
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return new ChromeDriver(options);
    }

    private static WebDriver firefox(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("signon.rememberSignons", false);
        profile.setPreference("signon.management.page.breach-alerts.enabled", false);
        options.setProfile(profile);

        return new FirefoxDriver(options);
    }

    private static WebDriver edge(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.setExperimentalOption("prefs", passwordManagerPrefs());
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return new EdgeDriver(options);
    }

    private static Map<String, Object> passwordManagerPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        return prefs;
    }
}
