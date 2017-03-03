package com.fxiaoke.dataplatform.crm.cleantool.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * 改工具类用于从web容器中获取服务bean的实例
 * Created by weilei on 2016/5/25.
 */
public class BeanFactoryUtil<T> {
    public static Object getBean(String beanName){
        WebApplicationContext webContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext sc = webContext.getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sc);
        return ac.getBean(beanName);
    }
//    private static final BeanFactoryUtil instance = new BeanFactoryUtil();
//    private ApplicationContext ac = null;
//    private BeanFactoryUtil(){
//        WebApplicationContext webContext = ContextLoader.getCurrentWebApplicationContext();
//        ServletContext sc = webContext.getServletContext();
//        ac = WebApplicationContextUtils.getWebApplicationContext(sc);
//    }
//    public static BeanFactoryUtil getIns(){
//        return instance;
//    }
//
//    public T getBean(Class cls){
//        String name = cls.getSimpleName();
//        String beanName = name.charAt(0)+name.substring(1,name.length());
//        T bean = (T)ac.getBean(beanName);
//        return bean;
//    }
}
