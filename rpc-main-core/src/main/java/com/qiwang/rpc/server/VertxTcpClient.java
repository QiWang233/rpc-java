package com.qiwang.rpc.server;

import cn.hutool.core.util.IdUtil;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.model.RpcRequest;
import com.qiwang.rpc.model.RpcResponse;
import com.qiwang.rpc.model.ServiceMetaInfo;
import com.qiwang.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx Tcp 客户端请求
 */
public class VertxTcpClient {

    /**
     * 发送请求
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(8081, "localhost",
//        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    // 连接成功返回一个socket对象
                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> requestProtocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum
                            .getEnumByValue(RpcApplication.getRpcConfig().getSerializer())
                            .getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    // 生成全局请求ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    requestProtocolMessage.setHeader(header);
                    requestProtocolMessage.setBody(rpcRequest);

                    // 对请求编码
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(requestProtocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息发送时编码错误");
                    }

                    // 接受响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                    (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息接收时编码错误");
                        }
                    });
                    socket.handler(bufferHandlerWrapper);
                });
        // 这行代码阻塞当前线程，直到CompletableFuture完成，并获取RPC响应。
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭连接
        netClient.close();
        return rpcResponse;
    }
}
