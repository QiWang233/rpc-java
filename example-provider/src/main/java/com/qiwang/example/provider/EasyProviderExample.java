package com.qiwang.example.provider;

import com.qiwang.rpc.server.HttpServer;
import com.qiwang.rpc.server.VertxHttpServer;

import java.util.HashMap;

public class EasyProviderExample {
    public static void main(String[] args) {
        //provide service
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
