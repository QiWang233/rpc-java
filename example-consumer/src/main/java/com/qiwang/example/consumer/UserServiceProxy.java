package com.qiwang.example.consumer;

import com.qiwang.example.common.model.User;
import com.qiwang.example.common.service.UserService;
import com.qiwang.rpc.model.RpcRequest;
import com.qiwang.rpc.model.RpcResponse;
import com.qiwang.rpc.serializer.JdkSerializer;
import com.qiwang.rpc.serializer.Serializer;

import java.io.IOException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();
        
        // 发请求 @builder允许链式赋值  .builder()...build()
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();


        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            // try-with-source
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                        .body(bodyBytes)
                        .execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
