package com.torm.orm.query.criteria.criterion.projection;

import com.torm.orm.query.criteria.Criteria;

public interface Projection {
    Projection as(String alias);

    String toSqlString(Criteria criteria);
}
