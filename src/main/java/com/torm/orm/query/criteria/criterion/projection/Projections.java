package com.torm.orm.query.criteria.criterion.projection;

public class Projections {
    public static Projection rowCount() {
        return new RowCountProjection();
    }

    public static CountProjection count(String propertyName) {
        return new CountProjection(propertyName);
    }

    public static CountProjection countDistinct(String propertyName) {
        return (new CountProjection(propertyName)).setDistinct();
    }

    public static AggregateProjection max(String propertyName) {
        return new AggregateProjection("max", propertyName);
    }

    public static AggregateProjection min(String propertyName) {
        return new AggregateProjection("min", propertyName);
    }

    public static AggregateProjection avg(String propertyName) {
        return new AvgProjection(propertyName);
    }

    public static AggregateProjection sum(String propertyName) {
        return new AggregateProjection("sum", propertyName);
    }

    public static Projection alias(Projection projection, String alias) {
        return new AliasedProjection(projection, alias);
    }
}
