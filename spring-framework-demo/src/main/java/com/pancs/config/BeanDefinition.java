package com.pancs.config;

//bean定义，存放bean解析的结果
public class BeanDefinition {

    private String scope;//作用域  是单例bean(singleton)还是原型bean(prototype)
    private boolean isLazy = false;//是否懒加载 默认false，非懒加载
    private Class beanClazz;//bean的全限定名

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }

    public Class getBeanClazz() {
        return beanClazz;
    }

    public void setBeanClazz(Class beanClazz) {
        this.beanClazz = beanClazz;
    }
}
