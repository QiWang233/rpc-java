package com.qiwang.example.provider;

import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.registry.LocalRegistry;
import com.qiwang.rpc.server.HttpServer;
import com.qiwang.rpc.server.VertxHttpServer;

import java.util.HashMap;
import java.util.Locale;

public class EasyProviderExample {
    public static void main(String[] args) {

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

//      provide service
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
