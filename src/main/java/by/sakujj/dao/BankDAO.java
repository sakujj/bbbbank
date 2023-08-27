package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Bank;
import by.sakujj.util.SQLQueries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankDAO implements DAO<Bank, Long> {

    private static final BankDAO INSTANCE = new BankDAO();

    public static BankDAO getInstance() {
        return INSTANCE;
    }

    private static final String TABLE_NAME = "Bank";
    private static final String ID_COLUMN_NAME = "bank_id";
    private static final List<String> ATTRIBUTES_WITHOUT_ID = List.of(
            "name"
    );

    private static final String FIND_BY_ID = SQLQueries.getSelectById(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String FIND_ALL = SQLQueries.getSelectAll(TABLE_NAME);

    private static final String UPDATE_BY_ID = SQLQueries.getUpdateById(
            TABLE_NAME,
            ID_COLUMN_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    private static final String DELETE_BY_ID = SQLQueries.getDeleteById(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String INSERT_BY_ID = SQLQueries.getInsert(
            TABLE_NAME,
            List.of("bank_id", "name")
    );

    @Override
    public Optional<Bank> findById(Long id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                    newBank(resultSet)
                );
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Bank> findAll(Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)){

            ResultSet resultSet = statement.executeQuery();

            List<Bank> all= new ArrayList<>();
            while (resultSet.next()) {
                all.add(newBank(resultSet));
            }
            return all;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Long save(Bank obj, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BY_ID)){
            statement.setObject(1, obj.getId());
            statement.setObject(2, obj.getName());

            statement.executeUpdate();

            return obj.getId();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(Bank obj, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID)) {
            statement.setObject(1, obj.getName());
            statement.setObject(2, obj.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean deleteById(Long id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {

            statement.setObject(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public static Bank newBank(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("bank_id", Long.class);
        String name = resultSet.getObject("name", String.class);
        return Bank.builder()
                .id(id)
                .name(name)
                .build();
    }
}
