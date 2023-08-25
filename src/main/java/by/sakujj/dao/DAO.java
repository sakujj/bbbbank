package by.sakujj.dao;

import by.sakujj.exceptions.DaoException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DAO<T, K> {
    Optional<T> findById(K id, Connection connection) throws DaoException;
    List<T> findAll(Connection connection) throws DaoException;
    K save(T obj, Connection connection);
    void update(T obj, Connection connection);
    void deleteById(K id, Connection connection);
}
