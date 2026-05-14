package e2e.data;

import java.math.BigDecimal;

public record Product(String name, BigDecimal price, String description) {}
