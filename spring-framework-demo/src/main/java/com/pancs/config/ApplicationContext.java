package com.pancs.config;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

    private Class configClazz;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();//bean定义的集合

    private Map<String, Object> singletonObjects = new HashMap<>();//单例池

    public ApplicationContext(Class configClazz) {
        this.configClazz = configClazz;
        //扫描
        scanBean(configClazz);
        //创建非懒加载的单例bean
        createNonLazySingleton();

    }

    //创建非懒加载的单例bean
    private void createNonLazySingleton() {
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (!beanDefinition.isLazy() && beanDefinition.getScope().contains("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName, bean);//存放到单例池
            }
        }
    }

    //创建bean
    private Object createBean(BeanDefinition beanDefinition) {
        Class beanClazz = beanDefinition.getBeanClazz();
        try {
            Object bean = beanClazz.getConstructor().newInstance();
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    //扫描bean
    private void scanBean(Class configClazz) {
        if (configClazz.isAnnotationPresent(ComponentScan.class)) {
            //解析扫描路径下class文件，获取所有的bean信息
            ComponentScan componentScanAnnotation = (ComponentScan) configClazz.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();//扫描路径
            path = path.replace(".", "/");
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File fileList = new File(resource.getFile());
            File[] files = fileList.listFiles();
            for (File f : files) {
                String s = f.getAbsolutePath();
                if (s.endsWith(".class")) {
                    s = s.substring(s.indexOf("com"), s.indexOf(".class"));
                    s = s.replace("\\", ".");
                    try {
                        Class clazz = classLoader.loadClass(s);
                        //判断这是不是一个bean
                        if (clazz.isAnnotationPresent(Component.class)) {
                            Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
                            String beanName = componentAnnotation.value();
                            BeanDefinition beanDefinition = new BeanDefinition();//bean定义，存放bean解析的结果
                            beanDefinition.setBeanClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = (Scope) clazz.getAnnotation(Scope.class);
                                String scope = scopeAnnotation.value();
                                beanDefinition.setScope(scope);
                            }
                            if (clazz.isAnnotationPresent(Lazy.class)) {
                                beanDefinition.setLazy(true);
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //获取bean
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new Exception("No such bean");
        } else {
            if ("singleton".equals(beanDefinition.getScope())) {
                //单例bean
                if (!beanDefinition.isLazy()) {
                    //非懒加载单例bean直接从单例池取
                    return singletonObjects.get(beanName);
                } else {
                    //懒加载的单例bean第一次使用的时候创建,创建后放入单例池，之后不用再重新创建
                    if (singletonObjects.get(beanName) == null) {
                        Object bean = createBean(beanDefinition);
                        singletonObjects.put(beanName,bean);//第一次使用的时候创建,创建后放入单例池，之后不用再重新创建
                        return bean;
                    } else {
                        return singletonObjects.get(beanName);
                    }
                }
            } else if ("prototype".equals(beanDefinition.getScope())) {
                //原型bean，每次获取都会创建一个新的对象
                return createBean(beanDefinition);
            }
        }
        return null;
    }

}
