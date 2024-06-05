package com.qiwang.example.provider;

import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.config.RegistryConfig;
import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.model.ServiceMetaInfo;
import com.qiwang.rpc.registry.LocalRegistry;
import com.qiwang.rpc.registry.Registry;
import com.qiwang.rpc.registry.RegistryFactory;
import com.qiwang.rpc.server.HttpServer;
import com.qiwang.rpc.server.VertxHttpServer;
import com.qiwang.rpc.utils.ConfigUtils;

public class ProviderExample {
    public static void main(String[] args) {
        // RPC框架初始化，初始化参数
        RpcApplication.init();
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
