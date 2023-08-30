package test.unit.mappers;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dto.BankRequest;
import by.sakujj.dto.BankResponse;
import by.sakujj.mappers.BankMapper;
import by.sakujj.model.Bank;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class BankMapperTests {
    private static final ApplicationContext context = ApplicationContext.getTestInstance();

    private static final BankMapper bankMapper = context.getByClass(BankMapper.class);

    @ParameterizedTest
    @MethodSource
    void toBank(BankRequest bankRequest, Bank expected) {
        Bank actual = bankMapper.fromRequest(bankRequest);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toBank() {
        final String name1 = "NAME-X";
        final String name2 = "XCXCXCXCX";
        return Stream.of(
                arguments(
                        BankRequest.builder()
                                .name(name1)
                                .build(),
                        Bank.builder()
                                .name(name1)
                                .id(null)
                                .build()
                ),
                arguments(
                        BankRequest.builder()
                                .name(name2)
                                .build(),
                        Bank.builder()
                                .name(name2)
                                .id(null)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void toBankResponse(Bank bank, BankResponse expected) {
        BankResponse actual = bankMapper.toResponse(bank);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toBankResponse() {
        final String name1 = "NAME-X";
        final Long id1 = 1L;
        final String name2 = "XCXCXCXCX";
        final Long id2 = 656L;
        return Stream.of(
                arguments(
                        Bank.builder()
                                .name(name1)
                                .id(id1)
                                .build(),
                        BankResponse.builder()
                                .name(name1)
                                .id(id1)
                                .build()
                ),
                arguments(
                        Bank.builder()
                                .name(name2)
                                .id(id2)
                                .build(),
                        BankResponse.builder()
                                .name(name2)
                                .id(id2)
                                .build()
                )
        );
    }
}
