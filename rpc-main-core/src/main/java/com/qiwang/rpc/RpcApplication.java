package com.qiwang.rpc;

import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.constant.RpcConstant;
import com.qiwang.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Rpc 框架引引用配置
 * 存放项目全局用到的变量。 使用“双检索单例模式”实现
 */
@Slf4j
public class RpcApplication {

    /**
     * 变量使用了 volatile 关键字修饰，确保多个线程之间的可见性，以避免线程间的数据不一致问题。
     */
    private static volatile RpcConfig rpcConfig;

    /**
     * 自定义配置初始化
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class){
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}