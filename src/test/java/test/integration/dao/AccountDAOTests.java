package test.integration.dao;

import by.sakujj.dao.AccountDAO;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Account;
import by.sakujj.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import test.integration.connection.AbstractConnectionRelatedTests;
import test.integration.connection.Rollback;

import static org.assertj.core.api.Assertions.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class AccountDAOTests extends AbstractConnectionRelatedTests {

    private static AccountDAO accountDAO = AccountDAO.getInstance();

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @ParameterizedTest
        @MethodSource
        void shouldContainAllSpecified(List<Account> accounts) throws DAOException {
            List<Account> all = accountDAO.findAll(getConnection());

            assertThat(all).containsAll(accounts);
        }

        static Stream<List<Account>> shouldContainAllSpecified() {
            return Stream.of(
                    List.of(
                            Account.builder()
                                    .id("123412341234123412341234XX00")
                                    .bankId(12345678910L)
                                    .clientId(1L)
                                    .moneyAmount(new BigDecimal("2000.00"))
                                    .dateWhenOpened(LocalDate.parse("2020-01-10"))
                                    .currency(Currency.USD)
                                    .build(),
                            Account.builder()
                                    .id("123412341234123412341234XX01")
                                    .bankId(12345678911L)
                                    .clientId(2L)
                                    .moneyAmount(new BigDecimal("2000.10"))
                                    .dateWhenOpened(LocalDate.parse("2020-02-11"))
                                    .currency(Currency.BYN)
                                    .build(),
                            Account.builder()
                                    .id("123412341234123412341234XX21")
                                    .bankId(12345678912L)
                                    .clientId(2L)
                                    .moneyAmount(new BigDecimal("2000.21"))
                                    .dateWhenOpened(LocalDate.parse("2020-04-11"))
                                    .currency(Currency.BYN)
                                    .build(),
                            Account.builder()
                                    .id("123412341234123412341234XX39")
                                    .bankId(12345678910L)
                                    .clientId(20L)
                                    .moneyAmount(new BigDecimal("2000.39"))
                                    .dateWhenOpened(LocalDate.parse("2020-04-19"))
                                    .currency(Currency.USD)
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
        void shouldReturnCorrectAccount(Account expected) throws DAOException {
            Optional<Account> actual = accountDAO.findById(expected.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Account> shouldReturnCorrectAccount() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @ParameterizedTest
        @ValueSource(strings = {"zzcc", "XX10XX"})
        void shouldReturnOptionalEmpty(String id) throws DAOException {
            Optional<Account> empty = accountDAO.findById(id, getConnection());

            assertThat(empty).isEmpty();
        }
    }

    @Nested
    @DisplayName("save (Account, Connection)")
    class save {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSaveAccount(Account accountToSave) throws DAOException {
            accountDAO.save(accountToSave, getConnection());
            Optional<Account> actual = accountDAO.findById(accountToSave.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(accountToSave);
        }

        static Stream<Account> shouldSaveAccount() {
            return findById.shouldReturnCorrectAccount().peek(
                    account -> account.setId("BOBY%s".formatted(account.getId().substring(4)))
            );
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotSaveAlreadyExisting(Account accountToSave) {
            assertThatThrownBy(() ->
                    accountDAO.save(accountToSave, getConnection())
            ).isInstanceOf(DAOException.class);
        }

        static Stream<Account> shouldNotSaveAlreadyExisting() {
            return findById.shouldReturnCorrectAccount().limit(3);
        }
    }

    @Nested
    @DisplayName("update (Account)")
    class update {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldUpdateAccount(Account accountToUpdate) throws DAOException {
            accountDAO.update(accountToUpdate, getConnection());
            Optional<Account> actual = accountDAO.findById(accountToUpdate.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(accountToUpdate);
        }

        static Stream<Account> shouldUpdateAccount() {
            return findById.shouldReturnCorrectAccount()
                    .peek(
                            account -> {
                                account.setCurrency(Currency.CNY);
                                account.setMoneyAmount(new BigDecimal("1444.40"));
                                account.setDateWhenOpened(LocalDate.now());
                                account.setBankId(12345678910L);
                                account.setClientId(8L);
                            }
                    );
        }

    }

    @Nested
    @DisplayName("deleteById (Long)")
    class deleteById {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldDeleteAccount(Account accountToDelete) throws DAOException {
            accountDAO.deleteById(accountToDelete.getId(), getConnection());
            Optional<Account> actual = accountDAO.findById(accountToDelete.getId(), getConnection());

            assertThat(actual).isEmpty();
        }

        static Stream<Account> shouldDeleteAccount() {
            return findById.shouldReturnCorrectAccount();
        }
    }
}
