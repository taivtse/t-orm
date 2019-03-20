package com.torm.orm.query.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NativeSQLStatement {
    PreparedStatement preparedStatement;

    NativeSQLStatement() {
    }

    public ResultSet executeQuery() throws SQLException {
        System.out.println(preparedStatement);
        return this.preparedStatement.executeQuery();
    }

    public Integer executeUpdate() throws SQLException {
        System.out.println(preparedStatement);
        return this.preparedStatement.executeUpdate();
    }

    public Long executeInsert() throws SQLException {
        System.out.println(preparedStatement);
        this.preparedStatement.executeUpdate();
        ResultSet resultSet = this.preparedStatement.getGeneratedKeys();

        if (resultSet != null && resultSet.next()) {
            return resultSet.getLong(1);
        }

        return null;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setParamAt(int index, Object parameter) throws SQLException {
        preparedStatement.setObject(index, parameter);
    }
}
