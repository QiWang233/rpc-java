package com.qiwang.rpc.config;

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
}
