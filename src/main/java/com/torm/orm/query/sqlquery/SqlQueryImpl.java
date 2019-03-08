package com.torm.orm.query.sqlquery;

import com.torm.orm.mapper.ArrayMapper;
import com.torm.orm.query.statement.NamedParamStatement;
import com.torm.orm.session.util.CloseExecutorUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlQueryImpl implements SqlQuery {
    private NamedParamStatement statement;

    public SqlQueryImpl(Connection connection, String sql) {
        try {
            this.statement = new NamedParamStatement(connection, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Object[]> list() {
        List<Object[]> resultList = new ArrayList<>();
        try {
            ResultSet resultSet = this.statement.executeQuery();
            while (resultSet.next()) {
                resultList.add(ArrayMapper.toArray(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
        return resultList;
    }

    @Override
    public int executeUpdate() throws SQLException {
        try {
            return this.statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            this.close();
        }
    }

    @Override
    public void setParameter(int index, Object parameter) throws SQLException {
        this.statement.setParamAt(index, parameter);
    }

    @Override
    public void setParameter(String namedParam, Object parameter) throws SQLException {
        this.statement.setNamedParam(namedParam, parameter);
    }

    private void close() {
        try {
            CloseExecutorUtil.closeStatement(statement.getPreparedStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
