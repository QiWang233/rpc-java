package com.qiwang.example.provider;

import com.qiwang.example.common.model.User;
import com.qiwang.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("user name:" + user.getName());
        return user;
    }
}
