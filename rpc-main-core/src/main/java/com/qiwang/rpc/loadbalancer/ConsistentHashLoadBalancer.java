package com.qiwang.rpc.loadbalancer;

import com.qiwang.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性hash环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    /**
     * 服务器选择
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点环
        // 每次调用负载均衡器时，都会重新构造 Hash 环，这是为了能够即时处理节点的变化。
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                // 对每个有该服务的服务器地址设置100个虚拟节点
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" +i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取调用请求的hash值
        int hash = getHash(requestParams);

        // 选择最接近且大于等于调用请求hash值的虚拟节点
        // ceilingEntry 通常是指在有序映射（如 Java 中的 SortedMap 接口的实现）中查找最接近但不小于给定键的条目的方法。
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);

        if (entry == null) {
            // 如果没有大于等于调用请求hash值的虚拟节点，则返回环首部节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * Hash算法，可自行实现
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
