package com.qiwang.example.common.service;

import com.qiwang.example.common.model.User;

public interface UserService {

    User getUser(User user);

    /**
     * 测试mock用的默认方法（java8后）
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
