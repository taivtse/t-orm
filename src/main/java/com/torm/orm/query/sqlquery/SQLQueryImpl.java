package com.torm.orm.query.sqlquery;

import com.torm.orm.exception.TormException;
import com.torm.orm.mapper.ArrayMapper;
import com.torm.orm.mapper.EntityMapper;
import com.torm.orm.query.statement.NamedParamStatement;
import com.torm.orm.session.util.CloseExecutorUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLQueryImpl implements SQLQuery {
    private Class entityClass;
    private NamedParamStatement statement;

    public SQLQueryImpl(Connection connection, String sql) {
        try {
            this.statement = new NamedParamStatement(connection, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List list() {
        List<Object> resultList = new ArrayList<>();
        try {
            ResultSet resultSet = this.statement.executeQuery();
            while (resultSet.next()) {
                if (entityClass != null) {
                    resultList.add(EntityMapper.of(this.entityClass).toEntity(resultSet));
                } else {
                    resultList.add(ArrayMapper.toArray(resultSet));
                }
            }
        } catch (Exception e) {
            throw new TormException(e);
        } finally {
            this.close();
        }
        return resultList;
    }

    @Override
    public int executeUpdate() throws SQLException {
        try {
            return this.statement.executeUpdate();
        } finally {
            this.close();
        }
    }

    @Override
    public void setParam(int index, Object parameter) throws SQLException {
        this.statement.setParamAt(index, parameter);
    }

    @Override
    public void setParam(String namedParam, Object parameter) throws SQLException {
        this.statement.setNamedParam(namedParam, parameter);
    }

    @Override
    public void setParamMap(Map<String, Object> paramMap) throws SQLException {
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            this.setParam(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public SQLQuery setEntity(Class entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    private void close() {
        try {
            CloseExecutorUtil.closeStatement(statement.getPreparedStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
