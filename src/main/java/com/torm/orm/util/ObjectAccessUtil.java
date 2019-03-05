package com.torm.orm.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectAccessUtil {
    public static Object getFieldData(Object object, Field field) throws ReflectiveOperationException {
        String fieldName = field.getName();
//            upper case the first letter of field name
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

//            build getter method name
        String getterMethodName = "get" + fieldName;

        Method getterMethod = object.getClass().getMethod(getterMethodName);
        return getterMethod.invoke(object);
    }

    public static void setFieldData(Object object, Object fieldValue, Field field) throws ReflectiveOperationException {
        String fieldName = field.getName();
//            upper case the first letter of field name
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

//            build setter method name
        String setterMethodName = "set" + fieldName;

//            get setter method and invoke
        Method setterMethod = object.getClass().getMethod(setterMethodName, field.getType());
        setterMethod.invoke(object, fieldValue);
    }

    public static Field getFieldByName(Class clazz, String fieldName) throws NoSuchFieldException {
        return clazz.getDeclaredField(fieldName);
    }
}
