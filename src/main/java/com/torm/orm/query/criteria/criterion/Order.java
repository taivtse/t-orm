package com.torm.orm.query.criteria.criterion;

public class Order {
    private String fragment;

    private Order(String fragment) {
        this.fragment = fragment;
    }

    public static Order asc(String propertyName) {
        return new Order(" ORDER BY " + propertyName + " ASC");
    }

    public static Order desc(String propertyName) {
        return new Order(" ORDER BY " + propertyName + " DESC");
    }

    public String toSqlString() {
        return fragment;
    }
}
