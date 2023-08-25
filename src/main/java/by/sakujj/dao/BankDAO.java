package by.sakujj.dao;

import by.sakujj.exceptions.DaoException;
import by.sakujj.model.Bank;
import by.sakujj.util.SqlQueries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String FIND_BY_ID = SqlQueries.getSelectById(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String FIND_ALL = SqlQueries.getSelectAll(TABLE_NAME);

    private static final String UPDATE_BY_ID = SqlQueries.getUpdateById(
            TABLE_NAME,
            ID_COLUMN_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    private static final String DELETE_BY_ID = SqlQueries.getDeleteById(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String INSERT_BY_ID = SqlQueries.getInsert(
            TABLE_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    @Override
    public Optional<Bank> findById(Long id, Connection connection) throws DaoException {
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
            throw new DaoException(e);
        }
    }

    @Override
    public List<Bank> findAll(Connection connection) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)){

            ResultSet resultSet = statement.executeQuery();

            List<Bank> all= new ArrayList<>();
            while (resultSet.next()) {
                all.add(newBank(resultSet));
            }
            return all;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Long save(Bank obj, Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Bank obj, Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id, Connection connection) {
        throw new UnsupportedOperationException();
    }

    private static Bank newBank(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("bank_id", Long.class);
        String name = resultSet.getObject("name", String.class);
        return Bank.builder()
                .id(id)
                .name(name)
                .build();
    }
}
