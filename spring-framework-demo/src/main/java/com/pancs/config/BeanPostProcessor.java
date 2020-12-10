package com.pancs.config;

//bean后置处理器
public interface BeanPostProcessor {

    public void setApplicationContext(ApplicationContext applicationContext);

    //实例化、依赖注入完毕，在调用显示的初始化之前完成一些定制的初始化任务
    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;


    /**
     * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     */
    //实例化、依赖注入、初始化完毕时执行
    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
