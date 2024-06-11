package com.qiwang.rpc.server;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertTcpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(socket -> {
            // 处理连接
            socket.handler(buffer -> {
                // 处理接受到的字节数组
                byte[] requestData = buffer.getBytes();
                // 在这里进行自定义的字节数组处理逻辑，比如解析请求·调用服务·构造响应等
                byte[] responseData = handleRequest(requestData);
                // 发送请求 buffer是Vert.x 为我们提供的字节数组缓冲区实现
                socket.write(Buffer.buffer(responseData));
            });
        });

        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port:" + port);
            } else {
                System.err.println("Failed to start TCP server:" + result.cause());
            }
        });
    }

    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        return "Hello, client!".getBytes();
    }

    public static void main(String[] args) {
        new VertTcpServer().doStart(8888);
    }
}
