package com.pancs;

import com.pancs.config.AppConfig;
import com.pancs.config.ApplicationContext;
import com.pancs.service.OrderService;
import com.pancs.service.StudentService;
import com.pancs.service.UserService;

public class Test {
    public static void main(String[] args) {
        System.out.println("--------Spring测试---------");
        //Spring启动,完成bean扫描，创建非懒加载的单例bean
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);

        try {
            UserService userService = (UserService) applicationContext.getBean("userService");
            UserService userService2 = (UserService) applicationContext.getBean("userService");
            OrderService orderService = (OrderService) applicationContext.getBean("orderService");
            OrderService orderService2 = (OrderService) applicationContext.getBean("orderService");
            StudentService studentService = (StudentService) applicationContext.getBean("studentService");
            StudentService studentService2 = (StudentService) applicationContext.getBean("studentService");
            System.out.println(userService);
            System.out.println(userService2);
            System.out.println(orderService);
            System.out.println(orderService2);
            System.out.println(studentService);
            System.out.println(studentService2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
