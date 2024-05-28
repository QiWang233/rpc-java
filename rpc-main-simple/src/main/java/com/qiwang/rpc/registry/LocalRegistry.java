package com.qiwang.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    /**
     * 注册信息储存
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();
}
