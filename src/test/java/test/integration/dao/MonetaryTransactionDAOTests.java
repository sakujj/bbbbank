package test.integration.dao;

import by.sakujj.dao.MonetaryTransactionDAO;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.model.MonetaryTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import test.integration.connection.AbstractConnectionRelatedTests;
import test.integration.connection.Rollback;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class MonetaryTransactionDAOTests extends AbstractConnectionRelatedTests {
    MonetaryTransactionDAO monetaryTransactionDAO = MonetaryTransactionDAO.getInstance();

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @ParameterizedTest
        @MethodSource
        void shouldContainAllSpecified(List<MonetaryTransaction> expectedToBeContained) throws DAOException {
            List<MonetaryTransaction> all = monetaryTransactionDAO.findAll(getConnection());

            assertThat(all).containsAll(expectedToBeContained);
        }

        static Stream<List<MonetaryTransaction>> shouldContainAllSpecified() {
            return Stream.of(
                    List.of(
                            MonetaryTransaction.builder()
                                    .id(1L)
                                    .timeWhenCommitted(LocalDateTime.parse("2021-04-20 04:04:55".replace(' ', 'T')))
                                    .senderAccountId("123412341234123412341234XX39")
                                    .receiverAccountId("123412341234123412341234XX30")
                                    .moneyAmount(new BigDecimal("1000.04"))
                                    .type(MonetaryTransactionType.TRANSFER)
                                    .build(),
                            MonetaryTransaction.builder()
                                    .id(2L)
                                    .timeWhenCommitted(LocalDateTime.parse("2021-04-20 04:04:55".replace(' ', 'T')))
                                    .senderAccountId("123412341234123412341234XX15")
                                    .moneyAmount(new BigDecimal("1000.04"))
                                    .type(MonetaryTransactionType.WITHDRAWAL)
                                    .build(),
                            MonetaryTransaction.builder()
                                    .id(3L)
                                    .timeWhenCommitted(LocalDateTime.parse("2021-04-20 04:04:55".replace(' ', 'T')))
                                    .receiverAccountId("123412341234123412341234XX10")
                                    .moneyAmount(new BigDecimal("6666.05"))
                                    .type(MonetaryTransactionType.DEPOSIT)
                                    .build()
                    )
            );
        }
    }

    @Nested
    @DisplayName("findById (Long, Connection)")
    class findById {
        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectTransaction(MonetaryTransaction expected) throws DAOException {
            Optional<MonetaryTransaction> actual = monetaryTransactionDAO.findById(expected.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<MonetaryTransaction> shouldReturnCorrectTransaction() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @ParameterizedTest
        @ValueSource(longs = {22L, 555L})
        void shouldReturnOptionalEmpty(Long id) throws DAOException {
            Optional<MonetaryTransaction> empty = monetaryTransactionDAO.findById(id, getConnection());

            assertThat(empty).isEmpty();
        }
    }

    @Nested
    @DisplayName("save (MonetaryTransaction, Connection)")
    class save {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSaveTransaction(MonetaryTransaction transactionToSave) throws DAOException {
            Long id = monetaryTransactionDAO.save(transactionToSave, getConnection());
            transactionToSave.setId(id);

            Optional<MonetaryTransaction> actual = monetaryTransactionDAO.findById(id, getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(transactionToSave);
        }

        static Stream<MonetaryTransaction> shouldSaveTransaction() {
            return findById.shouldReturnCorrectTransaction();
        }
    }

    @Nested
    @DisplayName("update (MonetaryTransaction, Connection")
    class update {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldUpdateTransaction(MonetaryTransaction transactionToUpdate) throws DAOException {
            monetaryTransactionDAO.update(transactionToUpdate, getConnection());

            Optional<MonetaryTransaction> actual = monetaryTransactionDAO.findById(transactionToUpdate.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(transactionToUpdate);
        }

        static Stream<MonetaryTransaction> shouldUpdateTransaction() {
            return findById.shouldReturnCorrectTransaction()
                    .peek(t -> {
                        t.setMoneyAmount(new BigDecimal("100000.00"));
                        t.setType(MonetaryTransactionType.DEPOSIT);
                        t.setSenderAccountId(null);
                        t.setReceiverAccountId("123412341234123412341234XX30");
                        t.setTimeWhenCommitted(LocalDateTime.parse("1995-12-14T04:01:22"));
                    });
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotUpdateNonExisting(MonetaryTransaction transactionToUpdate) throws DAOException {
            boolean isUpdated = monetaryTransactionDAO.update(transactionToUpdate, getConnection());

            assertThat(isUpdated).isFalse();
        }

        static Stream<MonetaryTransaction> shouldNotUpdateNonExisting() {
            return shouldUpdateTransaction()
                    .peek(t -> t.setId(t.getId() + 1234L));
        }
    }

    @Nested
    @DisplayName("deleteById (Long, Connection)")
    class delete {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldDeleteTransaction(Long idToDelete) throws DAOException {
            monetaryTransactionDAO.deleteById(idToDelete, getConnection());

            Optional<MonetaryTransaction> actual = monetaryTransactionDAO.findById(idToDelete, getConnection());

            assertThat(actual).isEmpty();
        }

        static LongStream shouldDeleteTransaction() {
            return findById.shouldReturnCorrectTransaction()
                    .mapToLong(MonetaryTransaction::getId);
        }

        @Rollback
        @ParameterizedTest
        @ValueSource(longs = {22L, 555L})
        void shouldNotDeleteNonExisting(Long idToDelete) throws DAOException {
            boolean isDeleted = monetaryTransactionDAO.deleteById(idToDelete, getConnection());

            assertThat(isDeleted).isFalse();
        }

    }
}
