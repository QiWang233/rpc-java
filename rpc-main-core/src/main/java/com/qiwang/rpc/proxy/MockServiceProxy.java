package com.qiwang.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（jdk动态代理）
 * implements InvocationHandler 可以理解为用于重写原类的反射调用方法
 */

@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock info {}", method.getName());

        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象 （用于日常测试）
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        if(type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == int.class) {
                return 0;
            }
        }

        return null;
    }
}
