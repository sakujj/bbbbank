package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.model.MonetaryTransactionType;
import by.sakujj.util.SQLQueries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonetaryTransactionDAO implements DAO<MonetaryTransaction, Long>  {

    private static final String TABLE_NAME = "MonetaryTransaction";
    private static final String ID_COLUMN_NAME = "monetary_transaction_id";
    private static final List<String> ATTRIBUTES_WITHOUT_ID = List.of(
            "time_when_committed",
            "sender_account_id",
            "receiver_account_id",
            "money_amount",
            "type"
    );

    private static final String FIND_BY_ID = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
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
    public Optional<MonetaryTransaction> findById(Long id, Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                        newMonetaryTransaction(resultSet)
                );
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<MonetaryTransaction> findAll(Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            List<MonetaryTransaction> all = new ArrayList<>();
            while (resultSet.next()) {
                all.add(newMonetaryTransaction(resultSet));
            }
            return all;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Long save(MonetaryTransaction obj, Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            MonetaryTransactionDAO.setStatementObjectsWithoutId(statement, obj);
            statement.executeUpdate();

            ResultSet generatedKey = statement.getGeneratedKeys();
            generatedKey.next();
            return generatedKey.getObject(1, Long.class);

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(MonetaryTransaction obj, Connection connection) throws DAOException {
        try(PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID)) {
            MonetaryTransactionDAO.setStatementObjectsWithoutId(statement, obj);
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

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static void setStatementObjectsWithoutId(PreparedStatement statement, MonetaryTransaction monetaryTransaction) throws SQLException {
        statement.setObject(1, monetaryTransaction.getTimeWhenCommitted());
        statement.setObject(2, monetaryTransaction.getSenderAccountId());
        statement.setObject(3, monetaryTransaction.getReceiverAccountId());
        statement.setObject(4, monetaryTransaction.getMoneyAmount());
        statement.setObject(5, monetaryTransaction.getType().toString());
    }

    public static MonetaryTransaction newMonetaryTransaction(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("monetary_transaction_id", Long.class);
        LocalDateTime timeWhenCommitted = resultSet.getObject("time_when_committed", LocalDateTime.class);
        String senderAccountId = resultSet.getObject("sender_account_id", String.class);
        String receiverAccountId = resultSet.getObject("receiver_account_id", String.class);
        BigDecimal moneyAmount = resultSet.getObject("money_amount", BigDecimal.class);
        MonetaryTransactionType type = MonetaryTransactionType.valueOf(resultSet.getObject("type", String.class));

        return MonetaryTransaction.builder()
                .id(id)
                .timeWhenCommitted(timeWhenCommitted)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .moneyAmount(moneyAmount)
                .type(type)
                .build();
    }
}
