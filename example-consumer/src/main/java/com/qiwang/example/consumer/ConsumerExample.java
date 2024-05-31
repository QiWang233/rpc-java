package com.qiwang.example.consumer;

import com.qiwang.rpc.config.RpcConfig;
import com.qiwang.rpc.utils.ConfigUtils;

public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
