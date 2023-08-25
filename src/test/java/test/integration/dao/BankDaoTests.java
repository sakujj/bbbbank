package test.integration.dao;

import by.sakujj.connectionpool.ConnectionPool;
import by.sakujj.dao.BankDAO;
import by.sakujj.model.Bank;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.integration.connectionpool.TestConnectionPool;
import test.integration.listeners.CloseTestConnectionPoolListener;

import java.sql.Connection;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.*;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class BankDaoTests {
    private final BankDAO bankDAO = BankDAO.getInstance();
    private final ConnectionPool connectionPool = TestConnectionPool.getInstance();


    @Nested
    @DisplayName("findById (Long id, Connection connection)")
    class findById_Long_Connection {
        @SneakyThrows
        @ParameterizedTest
        @MethodSource("shouldReturnRightBankSource")
        void shouldReturnRightBank(Long id, Bank expected) {
            try (Connection connection = connectionPool.getConnection()) {
                Bank actual = bankDAO.findById(id, connection).get();
                assertThat(actual).isEqualTo(expected);
            }
        }


        static Stream<Arguments> shouldReturnRightBankSource() throws ClassNotFoundException {
            Long id1 = 12345678910L;
            Long id2 = 12345678911L;
            Long id3 = 12345678914L;
            return Stream.of(
                    arguments(id1, Bank.builder()
                            .id(id1)
                            .name("Clever-bank")
                            .build()),
                    arguments(id2, Bank.builder()
                            .id(id2)
                            .name("Belbank")
                            .build()),
                    arguments(id3, Bank.builder()
                            .id(id3)
                            .name("Белорусский банкинг")
                            .build())
            );
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("shouldReturnOptionalEmptySource")
        void shouldReturnOptionalEmpty(Long id) {
            try (Connection connection = connectionPool.getConnection()) {
                Optional<Bank> actual = bankDAO.findById(id, connection);
                assertThat(actual).isEmpty();
            }
        }

        static LongStream shouldReturnOptionalEmptySource() {
            return LongStream.of(
                    245L,
                    12345679999L);
        }
    }

    @Nested
    @DisplayName("findAll (Connection connection)")
    class findAll {

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("shouldReturnAllBanksSource")
        void shouldReturnAllBanks(List<Bank> expected) {
            try (Connection connection = connectionPool.getConnection()) {
                List<Bank> actual = bankDAO.findAll(connection);
                assertThat(actual).containsAll(expected);
            }
        }

        static Stream<Arguments> shouldReturnAllBanksSource() {
            Long id1 = 12345678910L;
            Long id2 = 12345678911L;
            Long id3 = 12345678914L;
            Long id4 = 12345678912L;
            Long id5 = 12345678913L;
            return Stream.of(
                    arguments(
                            List.of(
                                    Bank.builder()
                                            .id(id1)
                                            .name("Clever-bank")
                                            .build(),
                                    Bank.builder()
                                            .id(id2)
                                            .name("Belbank")
                                            .build(),
                                    Bank.builder()
                                            .id(id3)
                                            .name("Белорусский банкинг")
                                            .build(),
                                    Bank.builder()
                                            .id(id4)
                                            .name("Uganda-bank")
                                            .build(),
                                    Bank.builder()
                                            .id(id5)
                                            .name("QWE banking")
                                            .build()
                            )
                    )
            );
        }
    }

}
