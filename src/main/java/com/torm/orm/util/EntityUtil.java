package com.torm.orm.util;

import com.torm.orm.annotation.Column;
import com.torm.orm.annotation.Entity;
import com.torm.orm.annotation.IdField;

import java.lang.reflect.Field;

public class EntityUtil {
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
            e.printStackTrace();
            throw new RuntimeException("No field with name: " + fieldName);
        }
    }

    public static Object getIdFieldData(Class<?> entityClass, Object entity) {
        try {
            String idFieldName = getIdFieldName(entityClass);
            Field idField = ObjectAccessUtil.getFieldByName(entityClass, idFieldName);
            return ObjectAccessUtil.getFieldData(entity, idField);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
