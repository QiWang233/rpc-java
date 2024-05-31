package com.qiwang.rpc.server;

import io.vertx.core.Vertx;

/**
 * Vertx http服务器
 */
public class VertxHttpServer implements HttpServer{

    /**
     * 启动服务器
     * @param port
     */
    public void doStart(int port) {
        // create vertx
        Vertx vertx = Vertx.vertx();
        
        // create http sever
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // setting watch port and response
//        server.requestHandler(request ->{
//            // manage http request
//            System.out.println("Received request: " + request.method() + " " + request.uri());
//
//            // send http response
//            request.response()
//                    .putHeader("content-type", "text/plain")
//                    .end("hello from Vert.x HTTP sever!");
//        });
        server.requestHandler(new HttpServerHandler());

        //start http server and listen port
        server.listen(port, result -> {
            if (result.succeeded()){
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Failed to start server: " + result.cause());
            }
        });
    }
}
