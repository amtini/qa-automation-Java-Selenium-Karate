package e2e.factories;

import e2e.data.CheckoutFormData;
import net.datafaker.Faker;

public final class DataFactory {

    private static final Faker faker = new Faker();

    private DataFactory() {}

    public static CheckoutFormData formData() {
        return new CheckoutFormData(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.address().zipCode()
        );
    }
}
