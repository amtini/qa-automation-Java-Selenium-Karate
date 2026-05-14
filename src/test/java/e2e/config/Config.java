package e2e.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final Properties props = loadProperties();
    private static final String env = resolveEnv();

    private Config() {}

    private static String resolveEnv() {
        String envProp = System.getProperty("env");
        return (envProp != null && !envProp.isBlank()) ? envProp : "dev";
    }

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on test classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
        return p;
    }

    private static String get(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        return props.getProperty(key);
    }

    public static String getEnv() {
        return env;
    }

    public static String getBrowser() {
        return get("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static String getBaseUrl() {
        // System property -DbaseUrl=... has highest priority
        String fromSystem = System.getProperty("baseUrl");
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        // Otherwise switch based on the env
        return switch (env) {
            case "staging" -> "https://staging.saucedemo.example.com/";
            case "mock"    -> "http://localhost:8080/";
            case "prod"    -> "https://www.saucedemo.com/";
            default        -> props.getProperty("baseUrl"); // dev (from config.properties)
        };
    }

    public static int getExplicitWaitSeconds() {
        return Integer.parseInt(get("explicitWaitSeconds"));
    }
}
