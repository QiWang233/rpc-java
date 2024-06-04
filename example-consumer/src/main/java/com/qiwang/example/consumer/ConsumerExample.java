package com.qiwang.example.consumer;

import com.qiwang.example.common.model.User;
import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.proxy.ServiceProxyFactory;
import com.qiwang.rpc.utils.ConfigUtils;

public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");

        // Mock代理
//        UserService userService = ServiceProxyFactory.getMockProxy(UserService.class);
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
        // 应该能看到输出的结果值为 0，而不是 1，说明调用了 MockServiceProxy 模拟服务代理。当然也可以通过 Debug 的方式进行验证
//        long number = userService.getNumber();
//        System.out.println(number);
    }

}
