package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Client;
import by.sakujj.util.SQLQueries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientDAO implements DAO<Client, Long> {

    private static final ClientDAO INSTANCE = new ClientDAO();

    public static ClientDAO getInstance() {
        return INSTANCE;
    }

    private static final String TABLE_NAME = "Client";
    private static final String ID_COLUMN_NAME = "client_id";
    private static final List<String> ATTRIBUTES_WITHOUT_ID = List.of(
            "username",
            "email",
            "password"
    );

    private static final String FIND_BY_ID = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String FIND_BY_EMAIL = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            "email"
    );

    private static final String FIND_ALL = SQLQueries.getSelectAll(TABLE_NAME);

    private static final String UPDATE_BY_ID = SQLQueries.getUpdateByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    private static final String DELETE_BY_ID = SQLQueries.getDeleteByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String INSERT = SQLQueries.getInsert(
            TABLE_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    @Override
    public Optional<Client> findById(Long id, Connection connection) throws DAOException {
        return ClientDAO.findByAttr(FIND_BY_ID, id, connection);
    }



    public Optional<Client> findByEmail(String email, Connection connection) throws DAOException {
        return ClientDAO.findByAttr(FIND_BY_EMAIL, email, connection);
    }

    @Override
    public List<Client> findAll(Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            List<Client> all = new ArrayList<>();
            while (resultSet.next()) {
                all.add(newClient(resultSet));
            }
            return all;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Long save(Client obj, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT,
                Statement.RETURN_GENERATED_KEYS)) {
            ClientDAO.setStatementObjectsWithoutId(statement, obj);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(Client obj, Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID)){
            ClientDAO.setStatementObjectsWithoutId(statement, obj);
            statement.setObject(ATTRIBUTES_WITHOUT_ID.size() + 1, obj.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }

    @Override
    public boolean deleteById(Long id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {
            statement.setObject(1, id);
            return  statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static void setStatementObjectsWithoutId(PreparedStatement statement, Client obj) throws SQLException {
        statement.setObject(1, obj.getUsername());
        statement.setObject(2, obj.getEmail());
        statement.setObject(3, obj.getPassword());
    }

    public static Client newClient(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("client_id", Long.class);
        String username = resultSet.getObject("username", String.class);
        String email = resultSet.getObject("email", String.class);
        String password = resultSet.getObject("password", String.class);

        return Client.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .build();
    }

    private static <T> Optional<Client> findByAttr(String queryToFindBy,
                                                   T attr,
                                                   Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(queryToFindBy)) {
            statement.setObject(1, attr);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(newClient(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
}
