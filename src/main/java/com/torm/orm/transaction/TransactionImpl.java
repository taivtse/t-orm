package com.torm.orm.transaction;

import com.torm.orm.exception.TormException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionImpl implements Transaction {
    private Connection connection;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void commit(){
        try {
            this.connection.commit();
        } catch (SQLException e) {
            throw new TormException(e);
        }
    }

    @Override
    public void rollback(){
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new TormException(e);
        }
    }
}
