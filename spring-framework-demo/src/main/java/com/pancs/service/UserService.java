package com.pancs.service;

import com.pancs.component.Autowired;
import com.pancs.component.Component;
import com.pancs.component.Resource;
import com.pancs.component.Scope;
import com.pancs.config.BeanNameAware;
import com.pancs.config.InitializingBean;

@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    @Resource
    private StudentService studentService;

    private String beanName;

    private String age;

    public void test1(){
        System.out.println("test1 orderService:"+orderService);
        System.out.println("test1 studentService:"+studentService);
        System.out.println("test1 beanName:"+beanName);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //对成员属性进行校验
        if (orderService == null) {
            throw new Exception("orderService is null");
        }
        if (beanName == null) {
            throw new Exception("beanName is null");
        }
    }
}
