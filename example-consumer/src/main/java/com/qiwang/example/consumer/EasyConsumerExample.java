package com.qiwang.example.consumer;

import com.qiwang.example.common.model.User;
import com.qiwang.example.common.service.UserService;

public class EasyConsumerExample {
    public static void main(String[] args) {
        // todo 需要获取 UserService 的实现类对象
        UserService userService = new UserServiceProxy();
        User user = new User();
        user.setName("qiwang");

        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null !!");
        }
    }
}
