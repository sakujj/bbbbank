package by.sakujj.context;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.connection.ConnectionPoolImpl;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.BankDAO;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dao.MonetaryTransactionDAO;
import by.sakujj.hashing.BCryptHasher;
import by.sakujj.hashing.Hasher;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.mappers.BankMapper;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.mappers.MonetaryTransactionMapper;
import by.sakujj.services.*;
import by.sakujj.validators.AccountValidator;
import by.sakujj.validators.BankValidator;
import by.sakujj.validators.ClientValidator;
import by.sakujj.validators.MonetaryTransactionValidator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static by.sakujj.connection.ConnectionPoolImpl.PROD_PROPERTIES;
import static by.sakujj.connection.ConnectionPoolImpl.TEST_PROPERTIES;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationContext {
    private final Map<Class<?>, Object> context = new HashMap<>();

    public static void cleanup(ApplicationContext context) {
        context.context.values()
                .stream()
                .filter(x -> x instanceof AutoCloseable)
                .map(x -> (AutoCloseable)x)
                .forEach(x -> {
                    try {
                        x.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static ApplicationContext instance;
    private static ApplicationContext testInstance;

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = newApplicationContext();
        }
        return instance;
    }

    public static ApplicationContext getTestInstance() {
        if (testInstance == null) {
            testInstance = newTestApplicationContext();
        }

        return testInstance;
    }

    public <T> T getByClass(Class<T> clazz) {
        return (T) context.get(clazz);
    }

    private static ApplicationContext newApplicationContext() {
        return createApplicationContext(PROD_PROPERTIES);
    }

    private static ApplicationContext newTestApplicationContext() {
        return createApplicationContext(TEST_PROPERTIES);
    }

    private static ApplicationContext createApplicationContext(String propertiesFileName) {
        ApplicationContext context = new ApplicationContext();

        context.put(Hasher.class, new BCryptHasher());
        context.put(ConnectionPool.class, new ConnectionPoolImpl(propertiesFileName));
        putDAO(context);
        putMappers(context);
        putServices(context);
        putValidators(context);

        return context;
    }

    private static void putDAO(ApplicationContext context) {
        context.put(AccountDAO.class, new AccountDAO());
        context.put(BankDAO.class, new BankDAO());
        context.put(ClientDAO.class, new ClientDAO());
        context.put(MonetaryTransactionDAO.class, new MonetaryTransactionDAO());
    }

    private static void putValidators(ApplicationContext context) {
        context.put(ClientValidator.class, new ClientValidator(
                context.getByClass(ClientService.class)
        ));
        context.put(BankValidator.class, new BankValidator(
                context.getByClass(BankService.class)
        ));
        context.put(AccountValidator.class, new AccountValidator(
                context.getByClass(BankService.class),
                context.getByClass(AccountService.class),
                context.getByClass(ClientService.class)
        ));
        context.put(MonetaryTransactionValidator.class, new MonetaryTransactionValidator(
                context.getByClass(AccountService.class)
        ));
    }
    private static void putMappers(ApplicationContext context) {
        context.put(BankMapper.class, Mappers.getMapper(BankMapper.class));

        MonetaryTransactionMapper monetaryTransactionMapper
                = Mappers.getMapper(MonetaryTransactionMapper.class);
        monetaryTransactionMapper.setAccountDAO(context.getByClass(AccountDAO.class));
        monetaryTransactionMapper.setBankDAO(context.getByClass(BankDAO.class));
        context.put(MonetaryTransactionMapper.class, monetaryTransactionMapper);

        AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
        accountMapper.setClientDAO(context.getByClass(ClientDAO.class));
        accountMapper.setAccountDAO(context.getByClass(AccountDAO.class));
        context.put(AccountMapper.class, accountMapper);

        ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);
        clientMapper.setHasher(context.getByClass(Hasher.class));
        context.put(ClientMapper.class, clientMapper);
    }
    private static void putServices(ApplicationContext context) {
        context.put(AccountService.class, new AccountServiceImpl(
                context.getByClass(ConnectionPool.class),
                context.getByClass(AccountMapper.class),
                context.getByClass(AccountDAO.class)
        ));
        context.put(AuthenticationService.class, new AuthenticationServiceImpl(
                context.getByClass(ClientDAO.class),
                context.getByClass(ClientMapper.class),
                context.getByClass(ConnectionPool.class),
                context.getByClass(Hasher.class)
        ));
        context.put(BankService.class, new BankServiceImpl(
                context.getByClass(ConnectionPool.class),
                context.getByClass(BankMapper.class),
                context.getByClass(BankDAO.class)
        ));
        context.put(ClientService.class, new ClientServiceImpl(
                context.getByClass(ConnectionPool.class),
                context.getByClass(ClientMapper.class),
                context.getByClass(ClientDAO.class)
        ));

        MonetaryTransactionService monetaryTransactionService
                = new MonetaryTransactionServiceReceiptDecorator(
                new MonetaryTransactionServiceImpl(
                        context.getByClass(MonetaryTransactionDAO.class),
                        context.getByClass(MonetaryTransactionMapper.class),
                        context.getByClass(AccountDAO.class),
                        context.getByClass(ConnectionPool.class)
                ),
                Executors.newFixedThreadPool(1)
        );
        context.put(MonetaryTransactionService.class, monetaryTransactionService);
    }

    private <T> void put(Class<T> clazz, T obj) {
        context.put(clazz, obj);
    }
}
