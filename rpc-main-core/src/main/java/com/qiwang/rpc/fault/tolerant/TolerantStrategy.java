package com.qiwang.rpc.fault.tolerant;

import com.qiwang.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     * @param context 上下文，用于传递数据
     * @return       异常
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
