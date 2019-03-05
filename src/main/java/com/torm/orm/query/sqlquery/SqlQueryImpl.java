package com.torm.orm.query.sqlquery;

import com.torm.orm.query.statement.NamedParamStatement;
import com.torm.orm.session.util.CloseExecutorUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

            List<Object> row = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnsCount; i++) {
                    row.add(resultSet.getObject(i));
                }

                resultList.add(row.toArray());
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
