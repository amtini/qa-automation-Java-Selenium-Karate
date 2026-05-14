package e2e.hooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestListener implements TestWatcher, BeforeTestExecutionCallback {

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(TestListener.class);
    private static final String START_KEY = "startTime";

    private static final List<Map<String, Object>> testResults = new ArrayList<>();
    private static long suiteStart = 0;

    static {
        // The shutdown hook fires once when the JVM exits, after ALL test classes
        // have finished. This is the only reliable way to write a single JSON
        // covering every test that ran, regardless of which class came last.
        Runtime.getRuntime().addShutdownHook(new Thread(TestListener::writeJson));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        long now = System.currentTimeMillis();
        context.getStore(NS).put(START_KEY, now);
        if (suiteStart == 0) {
            suiteStart = now;
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        long durationMs = computeDuration(context);
        printBanner("PASSED", context, durationMs, null, null);
        testResults.add(buildResult(context, "PASSED", durationMs, null, null));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        long durationMs = computeDuration(context);
        String category = categorize(cause);
        String screenshotPath = captureScreenshot(context);
        printBanner(category, context, durationMs, cause, screenshotPath);
        testResults.add(buildResult(context, category, durationMs, cause, screenshotPath));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        long durationMs = computeDuration(context);
        printBanner("SKIPPED", context, durationMs, cause, null);
        testResults.add(buildResult(context, "SKIPPED", durationMs, cause, null));
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        printBanner("SKIPPED", context, 0, null, null);
        testResults.add(buildResult(context, "SKIPPED", 0, null, null));
    }

    private long computeDuration(ExtensionContext context) {
        Long start = context.getStore(NS).get(START_KEY, Long.class);
        if (start == null) return 0;
        return System.currentTimeMillis() - start;
    }

    private String categorize(Throwable t) {
        if (t == null) return "FAILED";
        if (t instanceof AssertionError) return "BUG";
        return "FAILED";
    }

    private void printBanner(String status, ExtensionContext context, long durationMs,
                             Throwable t, String screenshotPath) {
        String name = context.getDisplayName();
        double seconds = durationMs / 1000.0;

        System.out.println();
        System.out.println("========================================");
        System.out.println("STATUS:  " + status);
        System.out.println("TEST:    " + name);
        System.out.printf( "TIME:    %.1fs%n", seconds);
        if (t != null) {
            System.out.println("REASON:  " + firstLine(t));
            System.out.println("AT:      " + locationOf(t));
        }
        if (screenshotPath != null) {
            System.out.println("SCREEN:  " + screenshotPath);
        }
        System.out.println("========================================");
        System.out.println();
    }

    private String firstLine(Throwable t) {
        String msg = t.getMessage();
        if (msg == null) return t.getClass().getSimpleName();
        int newline = msg.indexOf('\n');
        return newline > 0 ? msg.substring(0, newline) : msg;
    }

    private String locationOf(Throwable t) {
        for (StackTraceElement frame : t.getStackTrace()) {
            if (frame.getClassName().startsWith("e2e.") || frame.getClassName().startsWith("api.")) {
                return frame.getClassName() + ":" + frame.getLineNumber();
            }
        }
        StackTraceElement[] trace = t.getStackTrace();
        return trace.length > 0 ? trace[0].toString() : "unknown";
    }

    private String captureScreenshot(ExtensionContext context) {
        WebDriver driver = DriverManager.get();
        if (driver == null) return null;

        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String safeName = context.getDisplayName().replaceAll("\\W+", "_");
        String fileName = safeName + "-" + LocalDateTime.now().format(TIMESTAMP) + ".png";
        Path path = Paths.get("screenshots", fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            return path.toString().replace("\\", "/");
        } catch (IOException e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
            return null;
        }
    }

    private Map<String, Object> buildResult(ExtensionContext context, String status,
                                            long durationMs, Throwable t, String screenshotPath) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", context.getDisplayName());
        data.put("fullName",
                context.getRequiredTestClass().getName() + "." +
                context.getRequiredTestMethod().getName());
        data.put("status", status);
        data.put("durationMs", durationMs);
        data.put("startedAt", Instant.ofEpochMilli(System.currentTimeMillis() - durationMs).toString());

        if (t != null) {
            data.put("exceptionClass", t.getClass().getName());
            data.put("reason", firstLine(t));
            data.put("location", locationOf(t));
        }
        if (screenshotPath != null) {
            data.put("screenshot", screenshotPath);
        }
        return data;
    }

    private static void writeJson() {
        if (testResults.isEmpty()) return;

        long suiteEnd = System.currentTimeMillis();

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("suite", "QA Automation Suite");
        root.put("executedAt", Instant.ofEpochMilli(suiteStart).toString());
        root.put("durationMs", suiteEnd - suiteStart);

        Map<String, Long> totals = new LinkedHashMap<>();
        totals.put("passed",  count("PASSED"));
        totals.put("bugs",    count("BUG"));
        totals.put("failed",  count("FAILED"));
        totals.put("skipped", count("SKIPPED"));
        root.put("totals", totals);
        root.put("tests", testResults);

        Path outPath = Paths.get("target", "test-results.json");
        try {
            Files.createDirectories(outPath.getParent());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(outPath.toFile(), root);
            System.out.println();
            System.out.println("Test results JSON: " + outPath);
        } catch (IOException e) {
            System.err.println("Failed to write test results JSON: " + e.getMessage());
        }
    }

    private static long count(String status) {
        return testResults.stream().filter(r -> status.equals(r.get("status"))).count();
    }
}
