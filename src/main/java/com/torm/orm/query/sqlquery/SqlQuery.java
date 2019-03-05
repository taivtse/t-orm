package com.torm.orm.query.sqlquery;

import java.sql.SQLException;
import java.util.List;

public interface SqlQuery {
    List<Object[]> list();

    int executeUpdate() throws SQLException;

    void setParameter(int index, Object parameter) throws SQLException;

    void setParameter(String namedParam, Object parameter) throws SQLException;
}
