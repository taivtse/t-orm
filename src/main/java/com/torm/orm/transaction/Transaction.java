package com.torm.orm.transaction;

public interface Transaction {
    void commit();

    void rollback();
}
