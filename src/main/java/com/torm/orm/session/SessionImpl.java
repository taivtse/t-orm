package com.torm.orm.session;

import com.torm.orm.builder.StatementBuilder;
import com.torm.orm.query.criteria.Criteria;
import com.torm.orm.query.criteria.CriteriaImpl;
import com.torm.orm.query.criteria.criterion.Logical;
import com.torm.orm.query.sqlquery.SqlQuery;
import com.torm.orm.query.sqlquery.SqlQueryImpl;
import com.torm.orm.query.statement.NamedParamStatement;
import com.torm.orm.session.util.CloseExecutorUtil;
import com.torm.orm.transaction.Transaction;
import com.torm.orm.transaction.TransactionImpl;
import com.torm.orm.util.EntityUtil;
import com.torm.orm.util.ObjectAccessUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class SessionImpl implements Session {
    private Connection connection;
    NamedParamStatement statement;

    public SessionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T, ID> T get(Class<T> entityClass, ID id) {
        String idFieldName = EntityUtil.getIdFieldName(entityClass);
        Criteria criteria = this.createCriteria(entityClass);
        criteria.addWhere(Logical.and(idFieldName).eq(id));
        return (T) criteria.uniqueResult();
    }

    @Override
    public <T> T save(T entity) throws SQLException {
        Class entityClass = entity.getClass();
        String sql = StatementBuilder.buildInsertStatement(entity.getClass());
        statement = new NamedParamStatement(connection, sql);
        this.setEntityToStatement(entity, statement);
        Long generateId = statement.executeInsert();

//            lấy lại giá trị của entity trong trường hợp có những giá trị do trigger sinh ra.
        if (generateId != null) {
            entity = (T) this.get(entityClass, generateId);
        } else {
            Object id = EntityUtil.getIdFieldData(entityClass, entity);
            entity = (T) this.get(entityClass, id);
        }

        return entity;
    }

    @Override
    public <T> T update(T entity) throws SQLException {
        Class entityClass = entity.getClass();
        String sql = StatementBuilder.buildUpdateStatement(entity.getClass());
        statement = new NamedParamStatement(connection, sql);
        this.setEntityToStatement(entity, statement);
        Integer rowsEffect = statement.executeUpdate();

//            lấy lại giá trị của entity trong trường hợp có những giá trị do trigger sinh ra.
        if (rowsEffect > 0) {
            Object id = EntityUtil.getIdFieldData(entityClass, entity);
            entity = (T) this.get(entityClass, id);
        }

        return entity;
    }

    @Override
    public <T> void delete(T entity) throws SQLException {
        Class entityClass = entity.getClass();

//        lấy tên của field id và giá trị của id để set param cho câu statement
        Object id = EntityUtil.getIdFieldData(entityClass, entity);
        String idFieldName = EntityUtil.getIdFieldName(entityClass);

        String sql = StatementBuilder.buildDeleteStatement(entityClass);
        statement = new NamedParamStatement(connection, sql);
        statement.setNamedParam(idFieldName, id);
        statement.executeUpdate();
    }

    @Override
    public SqlQuery createSQLQuery(String sql) {
        return new SqlQueryImpl(connection, sql);
    }

    @Override
    public <T> Criteria createCriteria(Class<T> entityClass) {
        return new CriteriaImpl(connection, entityClass);
    }

    @Override
    public Transaction beginTransaction() {
        return new TransactionImpl(connection);
    }

    @Override
    public void close() {
        try {
            if (statement != null) {
                CloseExecutorUtil.closeStatement(statement.getPreparedStatement());
            }
            CloseExecutorUtil.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private <T> void setEntityToStatement(T entity, NamedParamStatement statement) throws SQLException {
        Class entityClass = entity.getClass();
        Field[] fieldList = entityClass.getDeclaredFields();

        for (Field field : fieldList) {
            Object fieldData;
            try {
                fieldData = ObjectAccessUtil.getFieldData(entity, field);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            statement.setNamedParam(field.getName(), fieldData);
        }
    }
}
