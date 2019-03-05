package com.torm.orm.query.criteria;

import com.torm.orm.mapper.EntityMapper;
import com.torm.orm.query.criteria.criterion.Criterion;
import com.torm.orm.query.criteria.criterion.NamedParam;
import com.torm.orm.query.criteria.criterion.Order;
import com.torm.orm.query.criteria.criterion.projection.Projection;
import com.torm.orm.query.statement.NamedParamStatement;
import com.torm.orm.session.util.CloseExecutorUtil;
import com.torm.orm.util.EntityUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CriteriaImpl implements Criteria {
    private Connection connection;
    private Class entityClass;
    private NamedParamStatement statement;

    private String genericQuery = "SELECT {selectColumns} FROM {tableName}{where}{groupBy}{having}{orderBy}{limit}{offset}";
    private String selectColumns = "";
    private String tableName = "";
    private StringBuilder where = new StringBuilder(" WHERE ");
    private String groupBy = "";
    private String having = "";
    private String orderBy = "";
    private String limit = "";
    private String offset = "";
    private boolean isMappedToEntity = true;

    private Map<String, NamedParam> namedParamMap = new TreeMap<>();

    public CriteriaImpl(Connection connection, Class entityClass) {
        this.connection = connection;
        this.entityClass = entityClass;
        this.selectColumns = "*";
        this.tableName = EntityUtil.getTableName(entityClass);
    }

    @Override
    public Class getEntityClass() {
        return this.entityClass;
    }

    @Override
    public Map<String, NamedParam> getNamedParamMap() {
        return namedParamMap;
    }

    @Override
    public List list() {
        ResultSet resultSet;
        List resultList = new ArrayList<>();
        try {
            resultSet = this.executeQuery();
            while (resultSet.next()) {
                if (isMappedToEntity) {
                    resultList.add(EntityMapper.of(this.entityClass).toEntity(resultSet));
                } else {
                    resultList.add(resultSet.getObject(1));
                }
            }

            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close();
        }

        return resultList;
    }

    @Override
    public Object uniqueResult() {
        ResultSet resultSet;
        Object object = null;
        try {
            this.setMaxResults(1);
            this.setFirstResult(0);
            resultSet = this.executeQuery();

            if (resultSet.next()) {
                if (isMappedToEntity) {
                    object = EntityMapper.of(this.entityClass).toEntity(resultSet);
                } else {
                    object = resultSet.getObject(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close();
        }

        return object;
    }

    @Override
    public Criteria addSelection(String fieldName) {
        if (this.selectColumns.equals("*")) {
            this.selectColumns = "";
        }
        if (!this.selectColumns.equals("*") && !this.selectColumns.isEmpty()) {
            this.selectColumns += ", ";
        }

        this.selectColumns += EntityUtil.getColumnName(entityClass, fieldName);
        return this;
    }

    @Override
    public Criteria add(Criterion criterion) {
        if (where.toString().equals(" WHERE ")) {
            criterion.setPrefixLogical("");
        }
        where.append(criterion.toSqlString(this));
        return this;
    }

    @Override
    public Criteria addOrder(Order order) {
        orderBy = order.toSqlString();
        return this;
    }

    @Override
    public Criteria setProjection(Projection projection) {
        selectColumns = projection.toSqlString(this);
        this.isMappedToEntity = false;
        return this;
    }

    @Override
    public Criteria setMaxResults(int limit) {
        this.limit = " LIMIT " + limit;
        return this;
    }

    @Override
    public Criteria setFirstResult(int offset) {
        this.offset = " OFFSET " + offset;
        return this;
    }

    private void close() {
        try {
            CloseExecutorUtil.closeStatement(statement.getPreparedStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String handleGenericQuery() {
        this.genericQuery = genericQuery.replace("{selectColumns}", selectColumns);
        this.genericQuery = genericQuery.replace("{tableName}", tableName);
        this.genericQuery = genericQuery.replace("{where}", where.toString().equals(" WHERE ") ? "" : where);
        this.genericQuery = genericQuery.replace("{groupBy}", groupBy);
        this.genericQuery = genericQuery.replace("{having}", having);
        this.genericQuery = genericQuery.replace("{orderBy}", orderBy);
        this.genericQuery = genericQuery.replace("{limit}", limit);
        this.genericQuery = genericQuery.replace("{offset}", offset);
        return this.genericQuery;
    }

    private ResultSet executeQuery() throws SQLException {
        ResultSet resultSet;
        String sql = this.handleGenericQuery();
        this.statement = new NamedParamStatement(connection, sql);
        this.statement.setNamedParamMap(namedParamMap);
        resultSet = this.statement.executeQuery();
        return resultSet;
    }
}
