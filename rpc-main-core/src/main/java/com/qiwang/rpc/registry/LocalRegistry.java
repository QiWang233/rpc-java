package com.qiwang.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    /**
     * 注册信息储存
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName, Class<?> implClass){
        map.put(serviceName, implClass);
    }

    /**
     * 获取
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }


}
