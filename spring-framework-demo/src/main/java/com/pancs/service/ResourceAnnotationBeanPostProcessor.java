package com.pancs.service;

import com.pancs.component.Component;
import com.pancs.component.Resource;
import com.pancs.config.ApplicationContext;
import com.pancs.config.ApplicationContextAware;
import com.pancs.config.BeanPostProcessor;

import java.lang.reflect.Field;

@Component("resourceAnnotationBeanPostProcessor")
public class ResourceAnnotationBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        //获取实例后，完成属性自动注入
        Class<?> beanClazz = bean.getClass();
        Field[] declaredFields = beanClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Resource.class)) {
                //byType --> byName
                String fieldName = field.getName();
                System.out.println("这是处理Resource注解的bean后置处理器!完成bean:"+beanName+"的field："+fieldName+"属性注入");
                field.setAccessible(true);
                field.set(bean,applicationContext.getBean(fieldName));
            }
        }
        return null;
    }
}
