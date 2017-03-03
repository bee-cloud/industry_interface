package com.fxiaoke.dataplatform.crm.cleantool.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by weilei on 2016/10/26.
 */
public class ReflectionUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    public static JSONArray convertToJSONArray(Object obj) {
        JSONArray jsonArray = new JSONArray();
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = descriptor.getReadMethod();
                jsonArray.add(getMethod.invoke(obj));
            }
        } catch (IntrospectionException e) {
            LOGGER.error("IntrospectionException...", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException... ", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException... ", e);
        }
        return jsonArray;
    }

    public static Object convertToJSONObject(Object obj) {
        JSONObject jsonObject = new JSONObject();
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = descriptor.getReadMethod();
                jsonObject.put(field.getName(),getMethod.invoke(obj));
            }
        } catch (IntrospectionException e) {
            LOGGER.error("IntrospectionException...", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException... ", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException... ", e);
        }
        return jsonObject;
    }

    public static Object getFieldValue(Object obj,String fieldName) {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                String name = field.getName();
                PropertyDescriptor descriptor = new PropertyDescriptor(name, clazz);
                Method getMethod = descriptor.getReadMethod();
                if(name.equals(fieldName))
                    return getMethod.invoke(obj);
            }
        } catch (IntrospectionException e) {
            LOGGER.error("IntrospectionException...", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException... ", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException... ", e);
        }
        return null;
    }



}
