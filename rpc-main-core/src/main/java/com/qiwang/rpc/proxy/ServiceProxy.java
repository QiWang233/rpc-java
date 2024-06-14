package com.qiwang.rpc.proxy;

import cn.hutool.aop.interceptor.SpringCglibInterceptor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.qiwang.rpc.RpcApplication;
import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.constant.RpcConstant;
import com.qiwang.rpc.fault.retry.RetryStrategy;
import com.qiwang.rpc.fault.retry.RetryStrategyFactory;
import com.qiwang.rpc.fault.tolerant.TolerantStrategy;
import com.qiwang.rpc.fault.tolerant.TolerantStrategyFactory;
import com.qiwang.rpc.loadbalancer.LoadBalancer;
import com.qiwang.rpc.loadbalancer.LoadBalancerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoaderBalancer());
            // 将调用方法名（请求路径） 作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            // 发送 TCP 请求
            // 使用重试机制
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();

        } catch (Exception e){
            throw new RuntimeException("调用反射失败");
        }
    }
}
