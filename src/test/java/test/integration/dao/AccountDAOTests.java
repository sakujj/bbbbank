package test.integration.dao;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dao.AccountDAO;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Account;
import by.sakujj.model.Client;
import by.sakujj.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import test.integration.connection.AbstractConnectionRelatedTests;
import test.integration.connection.Rollback;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class AccountDAOTests extends AbstractConnectionRelatedTests {
    private static final ApplicationContext context = ApplicationContext.getTestInstance();

    private final static AccountDAO accountDAO = context.getByClass(AccountDAO.class);

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
    @DisplayName("updateMoneyAmountById (BigDecimal, String, Connection)")
    class updateMoneyAmountById {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldAddMoneyAmount(BigDecimal moneyAmount, String accountId, BigDecimal expected) throws DAOException {
            accountDAO.updateMoneyAmountById(moneyAmount, accountId, getConnection());
            BigDecimal actual = accountDAO.findById(accountId, getConnection())
                    .get()
                    .getMoneyAmount();

            assertThat(actual).isEqualTo(expected);
        }

        static Stream<Arguments> shouldAddMoneyAmount() {
            return Stream.of(
                    arguments(new BigDecimal("1000.00"),
                            "123412341234123412341234XX01",
                            new BigDecimal("3000.10"))
            );
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSubtractMoneyAmount(BigDecimal moneyAmount, String accountId, BigDecimal expected) throws DAOException {
            accountDAO.updateMoneyAmountById(moneyAmount, accountId, getConnection());
            BigDecimal actual = accountDAO.findById(accountId, getConnection())
                    .get()
                    .getMoneyAmount();

            assertThat(actual).isEqualTo(expected);
        }

        static Stream<Arguments> shouldSubtractMoneyAmount() {
            return Stream.of(
                    arguments(new BigDecimal("-1000.00"),
                            "123412341234123412341234XX01",
                            new BigDecimal("1000.10"))
            );
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotSubtractMoreThanOneHas(BigDecimal moneyAmount, String accountId) throws DAOException {
            assertThatThrownBy( () ->
                    accountDAO.updateMoneyAmountById(
                            moneyAmount,
                            accountId,
                            getConnection())
            ).isInstanceOf(DAOException.class);
        }

        static Stream<Arguments> shouldNotSubtractMoreThanOneHas() {
            return Stream.of(
                    arguments(new BigDecimal("-10000.00"),
                            "123412341234123412341234XX01")
            );
        }
    }

    @Nested
    @DisplayName("findByClientId (Long, Connection)")
    class findByClientId {

        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectAccountsByClientId(Long id, List<Account> expected) throws DAOException {
            List<Account> actual = accountDAO.findByClientId(id, getConnection());

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        static Stream<Arguments> shouldReturnCorrectAccountsByClientId() {
            return Stream.of(arguments(
                    1L,
                    List.of(
                            Account.builder()
                                    .id("123412341234123412341234XX20")
                                    .clientId(1L)
                                    .bankId(12345678911L)
                                    .moneyAmount(new BigDecimal("2000.20"))
                                    .currency(Currency.USD)
                                    .dateWhenOpened(LocalDate.parse("2020-03-10"))
                                    .build(),
                            Account.builder()
                                    .id("123412341234123412341234XX00")
                                    .clientId(1L)
                                    .bankId(12345678910L)
                                    .moneyAmount(new BigDecimal("2000.00"))
                                    .currency(Currency.USD)
                                    .dateWhenOpened(LocalDate.parse("2020-01-10"))
                                    .build()
                    )
            ));
        }

        @ParameterizedTest
        @ValueSource(longs = {100L})
        void shouldReturnEmptyListByClientId(Long id) throws DAOException {
            List<Account> empty = accountDAO.findByClientId(id, getConnection());

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

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotUpdateNonExisting(Account accountToUpdate) throws DAOException {
            boolean isUpdated = accountDAO.update(accountToUpdate, getConnection());

            assertThat(isUpdated).isFalse();
        }

        static Stream<Account> shouldNotUpdateNonExisting() {
            return shouldUpdateAccount()
                    .peek(a -> a.setId(a.getId() + 1234L));
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

        @Rollback
        @ParameterizedTest
        @ValueSource(strings = {"22L", "555L"})
        void shouldNotDeleteNonExisting(String idToDelete) throws DAOException {
            boolean isDeleted = accountDAO.deleteById(idToDelete, getConnection());

            assertThat(isDeleted).isFalse();
        }
    }
}
