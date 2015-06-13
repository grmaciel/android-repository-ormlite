package br.com.persistence.repository;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by gilson.maciel on 27/04/2015.
 */
public interface IRepository<T, Id> {
    public void save(T entity) throws SQLException;
    public void saveBatch(List<T> entities) throws Exception;
    public List<T> queryAll() throws SQLException;
    public T findById(Id id) throws SQLException;
    public void delete(T entity) throws SQLException;
}
