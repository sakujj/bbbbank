package test.unit.util;

import by.sakujj.util.AccountIdGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class AccountIdGeneratorTests {

    @ParameterizedTest
    @MethodSource
    void generateAccountId(int accountNumber, Long bankId, Long clientId, String expectedId) {
        String accountId = AccountIdGenerator.generateAccountId(accountNumber, bankId, clientId);

        assertThat(accountId.length()).isEqualTo(28);
        assertThat(accountId).isEqualTo(expectedId);
    }

    static Stream<Arguments> generateAccountId() {
        int accountNumber = 30;
        Long clientId = 1234L;
        Long bankId = 12345678901L;

        long clientId2 = 372_036_854_775_807L;
        return Stream.of(arguments(
                accountNumber,
                        bankId, clientId,
                        "1234567890130XXXXXXXXXXX1234"
                ),
                arguments(
                        2,
                        bankId, clientId2,
                        "%d%s%d".formatted(bankId, "02", clientId2)
                )
        );
    }
}
