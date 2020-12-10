package com.pancs.config;

import com.pancs.component.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private Class configClazz;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();//bean定义的集合

    private Map<String, Object> singletonObjects = new HashMap<>();//单例池

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();//后置处理器集合

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
                Object bean = createBean(beanDefinition,beanName);
                singletonObjects.put(beanName, bean);//存放到单例池
            }
        }
    }

    //创建bean
    public Object createBean(BeanDefinition beanDefinition,String beanName) {
        Class beanClazz = beanDefinition.getBeanClazz();
        try {
            Object instance = beanClazz.getConstructor().newInstance();

            //获取实例后，完成属性自动注入 bean后置处理器
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.setApplicationContext(this);
                beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }
            //beanNameAware 设置beanName属性设置
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //beanNameAware 设置beanName属性设置
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            return instance;
        } catch (Exception e) {
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
                            //后置处理器
                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getConstructor().newInstance();
                                beanPostProcessorList.add(beanPostProcessor);
                            }
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
                    } catch (Exception e) {
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
            throw new Exception("No such bean"+",beanName:"+beanName);
        } else {
            if ("singleton".equals(beanDefinition.getScope())) {
                //单例bean
                if (!beanDefinition.isLazy()) {
                    //非懒加载单例bean直接从单例池取
                    Object bean = singletonObjects.get(beanName);
                    //为什么会没值？-->Sring创建bean是无序的，依赖自动注入的时候，依赖的bean可能还未创建，注入的时候需要创建出来
                    return bean == null ? createBean(beanDefinition,beanName) : bean;
                } else {
                    //懒加载的单例bean第一次使用的时候创建,创建后放入单例池，之后不用再重新创建
                    if (singletonObjects.get(beanName) == null) {
                        Object bean = createBean(beanDefinition,beanName);
                        singletonObjects.put(beanName,bean);//第一次使用的时候创建,创建后放入单例池，之后不用再重新创建
                        return bean;
                    } else {
                        return singletonObjects.get(beanName);
                    }
                }
            } else if ("prototype".equals(beanDefinition.getScope())) {
                //原型bean，每次获取都会创建一个新的对象
                return createBean(beanDefinition,beanName);
            }
        }
        return null;
    }

}
