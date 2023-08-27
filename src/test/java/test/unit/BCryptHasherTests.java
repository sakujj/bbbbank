package test.unit;

import by.sakujj.hashing.BCryptHasher;
import by.sakujj.hashing.Hasher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;


public class BCryptHasherTests {

    Hasher hasher = BCryptHasher.getInstance();

    @ParameterizedTest
    @ValueSource(strings = {"pass1", "qwertyzxcv32432@@"})
    void shouldHashAndThenVerify(String password) {
        String hashed = hasher.hash(password);
        boolean isVerified = hasher.verifyHash(password, hashed);

        assertThat(isVerified).isTrue();
    }
}
