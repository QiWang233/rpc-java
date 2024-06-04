package com.qiwang.example.provider;

import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.registry.LocalRegistry;
import com.qiwang.rpc.server.HttpServer;
import com.qiwang.rpc.server.VertxHttpServer;
import com.qiwang.rpc.utils.ConfigUtils;

public class ProviderExample {
    public static void main(String[] args) {
        // RPC框架初始化，初始化参数
//        RpcApplication.init();
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
