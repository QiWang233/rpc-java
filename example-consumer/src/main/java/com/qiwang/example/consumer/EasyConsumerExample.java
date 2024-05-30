package com.qiwang.example.consumer;

import com.qiwang.example.common.model.User;
import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        // 静态代理
//        UserService userService = new UserServiceProxy();
        // todo 需要获取 UserService 的实现类对象
//        UserService userService = null;
        User user = new User();
        user.setName("qiwang");
        // 调用
        User newUser = userService.getUser(user);

        if (newUser != null){
            System.out.println("调用成功");
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null !!");
        }
    }
}
