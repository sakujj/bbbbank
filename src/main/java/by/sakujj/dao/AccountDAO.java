package by.sakujj.dao;

import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Account;
import by.sakujj.model.Client;
import by.sakujj.model.Currency;
import by.sakujj.util.SQLQueries;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDAO implements DAO<Account, String> {

    private static final String TABLE_NAME = "Account";
    private static final String ID_COLUMN_NAME = "account_id";
    private static final List<String> ATTRIBUTES_WITHOUT_ID = List.of(
            "client_id",
            "bank_id",
            "money_amount",
            "currency_id",
            "date_when_opened"
    );

    private static final List<String> ATTRIBUTES_WITH_ID = List.of(
            "client_id",
            "bank_id",
            "money_amount",
            "currency_id",
            "date_when_opened",
            "account_id"
    );

    private static final String FIND_BY_ID = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String FIND_BY_CLIENT_ID = SQLQueries.getSelectByAttribute(
            TABLE_NAME,
            "client_id"
    );

    private static final String FIND_ALL = SQLQueries.getSelectAll(TABLE_NAME);

    private static final String UPDATE_BY_ID = SQLQueries.getUpdateByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME,
            ATTRIBUTES_WITHOUT_ID
    );

    private static final String UPDATE_MONEY_AMOUNT_BY_ID = """
            UPDATE Account
                SET money_amount = money_amount + ?
            WHERE account_id = ?
            """;

    private static final String DELETE_BY_ID = SQLQueries.getDeleteByAttribute(
            TABLE_NAME,
            ID_COLUMN_NAME
    );

    private static final String INSERT = SQLQueries.getInsert(
            TABLE_NAME,
            ATTRIBUTES_WITH_ID
    );


    public boolean updateMoneyAmountById(BigDecimal moneyAmount, String id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_MONEY_AMOUNT_BY_ID)) {
            statement.setObject(1, moneyAmount);
            statement.setObject(2, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public boolean updateMoneyAmountByPercentage(BigDecimal percentage, String id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE Account
                SET money_amount = money_amount + (money_amount * ?) / 100.0
                WHERE account_id = ?"""
        )) {
            statement.setObject(1, percentage);
            statement.setObject(2, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Account> findById(String id, Connection connection) throws DAOException {
        return findByAttr(FIND_BY_ID, id, connection).stream().findAny();
    }

    public List<Account> findByClientId(Long clientId, Connection connection) throws DAOException {
        return findByAttr(FIND_BY_CLIENT_ID, clientId, connection);
    }

    private static <T> List<Account> findByAttr(String queryToFindBy,
                                                T attr,
                                                Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(queryToFindBy)) {
            statement.setObject(1, attr);
            ResultSet resultSet = statement.executeQuery();

            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                accounts.add(newAccount(resultSet));
            }
            return accounts;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<Account> findAll(Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = statement.executeQuery();

            List<Account> all = new ArrayList<>();
            while (resultSet.next()) {
                all.add(newAccount(resultSet));
            }
            return all;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public String save(Account obj, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            AccountDAO.setStatementObjectsWithoutId(statement, obj);
            statement.setObject(ATTRIBUTES_WITH_ID.size(), obj.getId());
            statement.executeUpdate();

            return obj.getId();

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean update(Account obj, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID)) {
            AccountDAO.setStatementObjectsWithoutId(statement, obj);
            statement.setObject(ATTRIBUTES_WITH_ID.size(), obj.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }

    }

    @Override
    public boolean deleteById(String id, Connection connection) throws DAOException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {
            statement.setObject(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static void setStatementObjectsWithoutId(PreparedStatement statement, Account obj) throws SQLException {
        statement.setObject(1, obj.getClientId());
        statement.setObject(2, obj.getBankId());
        statement.setObject(3, obj.getMoneyAmount());
        statement.setObject(4, obj.getCurrency().toString());
        statement.setObject(5, obj.getDateWhenOpened());
    }

    public static Account newAccount(ResultSet resultSet) throws SQLException {
        String id = resultSet.getObject("account_id", String.class);
        Long clientId = resultSet.getObject("client_id", Long.class);
        Long bankId = resultSet.getObject("bank_id", Long.class);
        BigDecimal moneyAmount = resultSet.getObject("money_amount", BigDecimal.class);
        Currency currency = Currency.valueOf(resultSet.getObject("currency_id", String.class));
        LocalDate dateWhenOpened = resultSet.getObject("date_when_opened", LocalDate.class);
        return Account.builder()
                .id(id)
                .clientId(clientId)
                .bankId(bankId)
                .moneyAmount(moneyAmount)
                .currency(currency)
                .dateWhenOpened(dateWhenOpened)
                .build();
    }
}