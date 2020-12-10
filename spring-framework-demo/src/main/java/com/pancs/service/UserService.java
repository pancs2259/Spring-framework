package com.pancs.service;

import com.pancs.component.Autowired;
import com.pancs.component.Component;
import com.pancs.component.Scope;

@Component("userService")
@Scope("singleton")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test1(){
        System.out.println("test1:"+orderService);
    }

}
