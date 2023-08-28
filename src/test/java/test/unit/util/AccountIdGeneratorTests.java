package test.unit.util;

import by.sakujj.util.AccountIdGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class AccountIdGeneratorTests {

    @ParameterizedTest
    @MethodSource
    void generateAccountId(Long bankId, Long clientId, String expectedId) {
        String accountId = AccountIdGenerator.generateAccountId(bankId, clientId);
    }

    static Stream<Arguments> generateAccountId() {
        Long clientId = 1234L;
        Long bankId = 12345678901L;
        String filler = "X".repeat(17).substring(0, 17 - clientId.toString().length());

        long clientId2 = Long.MAX_VALUE % (10L * 17L);
        return Stream.of(arguments(
                        bankId, clientId,
                        bankId + filler + clientId
                ),
                arguments(bankId, clientId,
                        bankId.toString() + clientId2
                )
        );
    }
}
