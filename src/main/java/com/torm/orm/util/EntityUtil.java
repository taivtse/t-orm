package com.torm.orm.util;

import com.torm.orm.annotation.Column;
import com.torm.orm.annotation.Entity;
import com.torm.orm.annotation.Id;
import com.torm.orm.annotation.IdField;
import com.torm.orm.exception.TormException;

import java.lang.reflect.Field;

public class EntityUtil {
    public static boolean isAutoIncrement(Class<?> entityClass) {
        String idFieldName = getIdFieldName(entityClass);
        try {
            Field idField = ObjectAccessUtil.getFieldByName(entityClass, idFieldName);
            return idField.getAnnotation(Id.class).autoIncrement();
        } catch (NoSuchFieldException e) {
            throw new TormException(e);
        }
    }

    public static String getTableName(Class<?> entityClass) {
        return entityClass.getAnnotation(Entity.class).tableName();
    }

    public static String getIdColumnName(Class<?> entityClass) {
        String idFieldName = getIdFieldName(entityClass);
        return getColumnName(entityClass, idFieldName);
    }

    public static String getIdFieldName(Class<?> entityClass) {
        return entityClass.getAnnotation(IdField.class).name();
    }

    public static String getColumnName(Class<?> entityClass, String fieldName) {
        try {
            return entityClass.getDeclaredField(fieldName).getAnnotation(Column.class).name();
        } catch (NoSuchFieldException e) {
            throw new TormException("No field with name: " + fieldName, e);
        }
    }

    public static Object getIdFieldData(Class<?> entityClass, Object entity) {
        try {
            String idFieldName = getIdFieldName(entityClass);
            Field idField = ObjectAccessUtil.getFieldByName(entityClass, idFieldName);
            return ObjectAccessUtil.getFieldData(entity, idField);
        } catch (Exception e) {
            throw new TormException(e);
        }
    }
}
