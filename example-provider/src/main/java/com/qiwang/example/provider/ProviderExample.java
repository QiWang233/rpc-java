package com.qiwang.example.provider;

import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.registry.LocalRegistry;
import com.qiwang.rpc.server.HttpServer;
import com.qiwang.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init();

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
