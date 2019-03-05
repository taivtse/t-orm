package com.torm.orm.query.criteria.criterion.projection;

import com.torm.orm.query.criteria.Criteria;

public class AliasedProjection implements Projection {
    private final Projection projection;
    private final String alias;

    public AliasedProjection(Projection projection, String alias) {
        this.projection = projection;
        this.alias = alias;
    }

    @Override
    public String toSqlString(Criteria criteria) {
        return this.projection.toSqlString(criteria) + " AS " + this.alias;
    }
}
