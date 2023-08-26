package test.integration.dao;

import by.sakujj.dao.BankDAO;
import by.sakujj.exceptions.DaoException;
import by.sakujj.model.Bank;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.integration.connection.AbstractConnectionRelatedTests;
import test.integration.connection.Rollback;


import java.sql.SQLException;
import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PUBLIC)
public class BankDaoTests extends AbstractConnectionRelatedTests {
    private final BankDAO bankDAO = BankDAO.getInstance();

    @Nested
    @DisplayName("findById (Long, Connection)")
    class findById {

        @ParameterizedTest
        @MethodSource
        void shouldReturnRightBank(Long id, Bank expected) throws DaoException {
            Optional<Bank> actual = bankDAO.findById(id, getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Arguments> shouldReturnRightBank() {
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

        @ParameterizedTest
        @MethodSource
        void shouldReturnOptionalEmpty(Long id) throws DaoException {
            Optional<Bank> actual = bankDAO.findById(id, getConnection());

            assertThat(actual).isEmpty();
        }

        static LongStream shouldReturnOptionalEmpty() {
            return LongStream.of(
                    245L,
                    12345679999L);
        }
    }

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @ParameterizedTest
        @MethodSource
        void shouldReturnAllBanks(List<Bank> expected) throws DaoException {
            List<Bank> actual = bankDAO.findAll(getConnection());

            assertThat(actual).containsAll(expected);
        }

        static Stream<List<Bank>> shouldReturnAllBanks() {
            Long id1 = 12345678910L;
            Long id2 = 12345678911L;
            Long id3 = 12345678914L;
            Long id4 = 12345678912L;
            Long id5 = 12345678913L;
            return Stream.of(

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
            );
        }
    }

    @Nested
    @DisplayName("save (Bank, Connection)")
    class save {


        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSaveBank(Bank bankToSave) throws DaoException, SQLException {
            bankDAO.save(bankToSave, getConnection());
            Optional<Bank> actual = bankDAO.findById(bankToSave.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(bankToSave);
        }

        static Stream<Bank> shouldSaveBank() {
            return Stream.of(
                    Bank.builder()
                            .id(11122233330L)
                            .name("XXX BB")
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("deleteById (Long, Connection)")
    class deleteById {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldDeleteBank(Bank bankToDelete) throws DaoException {
            bankDAO.deleteById(bankToDelete.getId(), getConnection());
            Optional<Bank> bank = bankDAO.findById(bankToDelete.getId(), getConnection());

            assertThat(bank).isEmpty();
        }

        static Stream<Bank> shouldDeleteBank() {
            return Stream.of(
                    Bank.builder()
                            .id(12345678910L)
                            .name("Clever-bank")
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("update (Bank, Connection")
    class update {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldUpdateBank(Bank bankToUpdate) throws DaoException {
            bankDAO.update(bankToUpdate, getConnection());
            Optional<Bank> bankAfterUpdate = bankDAO.findById(bankToUpdate.getId(), getConnection());

            assertThat(bankAfterUpdate).isPresent();
            assertThat(bankAfterUpdate.get()).isEqualTo(bankToUpdate);
        }

        static Stream<Bank> shouldUpdateBank() {
            return Stream.of(
                    Bank.builder()
                            .id(12345678910L)
                            .name("xxxxxxxxxxx")
                            .build()
            );
        }
    }
}
