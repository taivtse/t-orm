package com.torm.orm.session;

import com.torm.orm.query.criteria.Criteria;
import com.torm.orm.query.sqlquery.SqlQuery;
import com.torm.orm.transaction.Transaction;

import java.io.Serializable;
import java.sql.SQLException;

public interface Session {
    <T> T get(Class<T> entityClass, Serializable id);

    void save(Object entity) throws SQLException;

    void update(Object entity) throws SQLException;

    void delete(Object entity) throws SQLException;

    SqlQuery createSQLQuery(String sql) throws SQLException;

    Criteria createCriteria(Class entityClass);

    Transaction beginTransaction();

    void close();
}
