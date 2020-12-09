package com.pancs;

import com.pancs.config.ApplicationContext;

public class Test {
    public static void main(String[] args) {
        System.out.println("--------Spring测试---------");
        //Spring启动,扫描
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);

        Object userService = applicationContext.getBean("userService");
        System.out.println(userService);

    }
}
