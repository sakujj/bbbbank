package test.integration.dao;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dao.ClientDAO;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Bank;
import by.sakujj.model.Client;
import by.sakujj.model.MonetaryTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import test.integration.connection.AbstractConnectionRelatedTests;
import test.integration.connection.Rollback;

import static org.assertj.core.api.Assertions.*;


import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Slf4j
public class ClientDAOTests extends AbstractConnectionRelatedTests {
    private static final ApplicationContext context = ApplicationContext.getTestInstance();

    private static final ClientDAO clientDAO = context.getByClass(ClientDAO.class);

    @Nested
    @DisplayName("findAll (Connection)")
    class findAll {

        @ParameterizedTest
        @MethodSource
        void shouldContainAllSpecified(List<Client> expectedToBeContained) throws DAOException {
            List<Client> all = clientDAO.findAll(getConnection());

            assertThat(all).containsAll(expectedToBeContained);
        }

        static Stream<List<Client>> shouldContainAllSpecified() {
            return Stream.of(
                    List.of(
                            Client.builder()
                                    .id(1L)
                                    .username("user0")
                                    .email("email0@gmail.com")
                                    .password("$2a$10$1mlM3e40rVQ8311QWex89Ozvy91BsmyVuM.bDbCcOjJJIUXMFpcMy")
                                    .build(),
                            Client.builder()
                                    .id(6L)
                                    .username("user5")
                                    .email("email5@gmail.com")
                                    .password("$2a$10$mUYfZSnWdad0Z6iwnEx5/uXbc/PKI1mmBMoa9tin5ygKMjdZ.j/Z.")
                                    .build(),
                            Client.builder()
                                    .id(15L)
                                    .username("user14")
                                    .email("email14@gmail.com")
                                    .password("$2a$10$9e7LEAAZjdpYR5a2XltkweDEyLRXrkflYaCiGtYXp59YqRIpmvHL2")
                                    .build(),
                            Client.builder()
                                    .id(20L)
                                    .username("user19")
                                    .email("email19@gmail.com")
                                    .password("$2a$10$OPBGJTztRwkxpAqDfCixje9Icg60ueQBeb53F4AyC8PleBpATk.He")
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
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            Optional<Client> actual = clientDAO.findById(expected.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @ParameterizedTest
        @MethodSource
        void shouldReturnOptionalEmpty(Long id) throws DAOException {
            Optional<Client> actual = clientDAO.findById(id, getConnection());

            assertThat(actual).isEmpty();
        }

        static LongStream shouldReturnOptionalEmpty() {
            return LongStream.of(999L, 666L);
        }
    }

    @Nested
    @DisplayName("findByEmail (String, Connection)")
    class findByEmail {

        @ParameterizedTest
        @MethodSource
        void shouldReturnCorrectClient(Client expected) throws DAOException {
            Optional<Client> actual = clientDAO.findByEmail(expected.getEmail(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(expected);
        }

        static Stream<Client> shouldReturnCorrectClient() {
            return findAll.shouldContainAllSpecified().flatMap(List::stream);
        }

        @ParameterizedTest
        @ValueSource(strings = {"emmmm@gdsg.ru"})
        void shouldReturnOptionalEmpty(String email) throws DAOException {
            Optional<Client> actual = clientDAO.findByEmail(email, getConnection());

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    @DisplayName("save (Client)")
    class save {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldSaveClient(Client clientToSave) throws DAOException {
            Long id = clientDAO.save(clientToSave, getConnection());
            clientToSave.setId(id);
            Optional<Client> actual = clientDAO.findById(id, getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(clientToSave);
        }

        static Stream<Client> shouldSaveClient() {
            return findById.shouldReturnCorrectClient()
                    .peek(client -> {
                                client.setEmail(
                                        client.getEmail()
                                                .replace(".", "TEST."));
                                client.setPassword("TEST" + client.getPassword().substring(4));
                            }
                    );
        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotSaveWithAlreadyTakenEmail(Client clientWithEmailAlreadyTaken) {
            assertThatThrownBy(() ->
                    clientDAO.save(clientWithEmailAlreadyTaken, getConnection())
            ).isInstanceOf(DAOException.class);
        }

        static Stream<Client> shouldNotSaveWithAlreadyTakenEmail() {
            return findById.shouldReturnCorrectClient().limit(3);
        }
    }

    @Nested
    @DisplayName("update (Client, Connection)")
    class update {

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldUpdateClient(Client clientToUpdate) throws DAOException {
            clientDAO.update(clientToUpdate, getConnection());
            Optional<Client> actual = clientDAO.findById(clientToUpdate.getId(), getConnection());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isEqualTo(clientToUpdate);
        }

        static Stream<Client> shouldUpdateClient() {
            return findById.shouldReturnCorrectClient().peek(
                    client -> {
                        client.setUsername("UPDATE-TEST %s".formatted(client.getUsername()));
                        client.setEmail("UPDATE-TEST %s".formatted(client.getEmail()));
                        client.setPassword("UPDT%s".formatted(client.getPassword().substring(4)));
                    }
            );
        }

        @Rollback
        @RepeatedTest(3)
        void shouldNotUpdateToAlreadyTakenEmail() throws DAOException {
            Client clientToUpdate = clientDAO.findById(1L, getConnection()).get();
            Random random = new Random();
            Long id = random.nextLong(2, 21);
            String alreadyTakenEmail = clientDAO.findById(id, getConnection()).get().getEmail();
            clientToUpdate.setEmail(alreadyTakenEmail);

            assertThatThrownBy(() ->
                    clientDAO.update(clientToUpdate, getConnection())
            ).isInstanceOf(DAOException.class);

        }

        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldNotUpdateNonExisting(Client clientToUpdate) throws DAOException {
            boolean isUpdated = clientDAO.update(clientToUpdate, getConnection());

            assertThat(isUpdated).isFalse();
        }

        static Stream<Client> shouldNotUpdateNonExisting() {
            return shouldUpdateClient()
                    .peek(c -> c.setId(c.getId() + 1234L));
        }
    }

    @Nested
    @DisplayName("deleteById (Long, Connection)")
    class deleteById {
        @Rollback
        @ParameterizedTest
        @MethodSource
        void shouldDeleteClient(Client clientToDelete) throws DAOException {
            clientDAO.deleteById(clientToDelete.getId(), getConnection());
            Optional<Client> empty = clientDAO.findById(clientToDelete.getId(), getConnection());

            assertThat(empty).isEmpty();
        }

        static Stream<Client> shouldDeleteClient() {
            return findById.shouldReturnCorrectClient().limit(3);
        }

        @Rollback
        @ParameterizedTest
        @ValueSource(longs = {22L, 555L})
        void shouldNotDeleteNonExisting(Long idToDelete) throws DAOException {
            boolean isDeleted = clientDAO.deleteById(idToDelete, getConnection());

            assertThat(isDeleted).isFalse();
        }
    }
}
