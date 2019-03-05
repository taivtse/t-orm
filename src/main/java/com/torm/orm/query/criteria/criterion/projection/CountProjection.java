package com.torm.orm.query.criteria.criterion.projection;

import com.torm.orm.query.criteria.Criteria;

public class CountProjection extends AggregateProjection {
    private boolean distinct;

    protected CountProjection(String propertyName) {
        super("count", propertyName);
    }

    public CountProjection setDistinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public String toSqlString(Criteria criteria) {
        StringBuilder superSqlString = new StringBuilder(super.toSqlString(criteria));
        if (this.distinct) {
            superSqlString.insert(superSqlString.indexOf("(") + 1, "DISTINCT ");
        }
        return superSqlString.toString();
    }
}
