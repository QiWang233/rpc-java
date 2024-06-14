package com.qiwang.rpc.fault.retry;

import com.qiwang.rpc.model.RpcResponse;

public class RetryStrategyTest {
    RetryStrategy retryStrategy = new NoRetryStrategy();

    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试 retry");
                throw new RuntimeException("模拟重试失败");
            });
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
