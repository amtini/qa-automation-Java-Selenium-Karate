package api.petstore;

import com.intuit.karate.junit5.Karate;
import e2e.hooks.TestListener;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TestListener.class)
class PetRunner {

    @Karate.Test
    Karate testPet() {
        return Karate.run("pet").relativeTo(getClass());
    }
}
