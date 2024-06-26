package com.qiwang.rpc.config;

import com.qiwang.rpc.fault.retry.RetryStrategyKeys;
import com.qiwang.rpc.fault.tolerant.TolerantStrategy;
import com.qiwang.rpc.fault.tolerant.TolerantStrategyKeys;
import com.qiwang.rpc.loadbalancer.LoadBalancer;
import com.qiwang.rpc.loadbalancer.LoadBalancerKeys;
import com.qiwang.rpc.serializer.Serializer;
import com.qiwang.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * Rpc框架配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8081;

    /**
     * mock 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loaderBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    public String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}

