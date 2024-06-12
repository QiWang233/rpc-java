package com.qiwang.rpc.proxy;

import cn.hutool.aop.interceptor.SpringCglibInterceptor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.constant.RpcConstant;
import com.qiwang.rpc.model.RpcRequest;
import com.qiwang.rpc.model.RpcResponse;
import com.qiwang.rpc.model.ServiceMetaInfo;
import com.qiwang.rpc.protocol.*;
import com.qiwang.rpc.registry.Registry;
import com.qiwang.rpc.registry.RegistryFactory;
import com.qiwang.rpc.serializer.JdkSerializer;
import com.qiwang.rpc.serializer.Serializer;
import com.qiwang.rpc.serializer.SerializerFactory;
import com.qiwang.rpc.server.VertxTcpClient;
import com.qiwang.rpc.server.VertxTcpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * jdk动态代理， http
 */
//public class ServiceProxy implements InvocationHandler {
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        // 指定序列化器
//        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
//
//        // 构造请求
//        String serviceName = method.getDeclaringClass().getName();
//        RpcRequest rpcRequest = RpcRequest.builder()
//                .serviceName(serviceName)
//                .methodName(method.getName())
//                .parameterTypes(method.getParameterTypes())
//                .args(args)
//                .build();
//
//        try{
//            // 序列化
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//
//            // 从注册中心获取服务提供者请求地址
//            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
//            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//            serviceMetaInfo.setServiceName(serviceName);
//            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
//            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
//
//            if (CollUtil.isEmpty(serviceMetaInfoList)) {
//                throw new RuntimeException("暂无服务地址");
//            }
//
//            // 暂时先取一个
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
//
//            // 发送请求  ** 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）
//            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()){
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//}

/**
 * jdk动态代理， tcp
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

//            if (CollUtil.isEmpty(serviceMetaInfoList)) {
//                throw new RuntimeException("暂无服务地址");
//            }
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            ServiceMetaInfo selectedServiceMetaInfo = new ServiceMetaInfo();
            // 发送 TCP 请求
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();

        } catch (Exception e){
            throw new RuntimeException("调用反射失败");
        }
    }
}
