package by.sakujj.connectionpool;

import by.sakujj.exceptions.DaoException;

import java.sql.Connection;

public interface ConnectionPool {
    Connection getConnection() throws DaoException;
    void close();
}
