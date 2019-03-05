package com.torm.orm.query.criteria.criterion.projection;

import com.torm.orm.query.criteria.Criteria;

public interface Projection {
    String toSqlString(Criteria criteria);
}
