package com.qiao;

/**
 * 配置类
 *
 * @author oldmanpushcart@gmail.com
 */
public class Configure {

    private String targetIp;                // 目标主机IP
    private int targetPort;                 // 目标进程号
    private int javaPid;                    // 对方java进程号
    private int connectTimeout = 6000;      // 连接超时时间(ms)
    private String bsiCore;
    private String bsiAgent;

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public int getJavaPid() {
        return javaPid;
    }

    public void setJavaPid(int javaPid) {
        this.javaPid = javaPid;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getBsiAgent() {
        return bsiAgent;
    }

    public void setBsiAgent(String bsiAgent) {
        this.bsiAgent = bsiAgent;
    }

    public String getBsiCore() {
        return bsiCore;
    }

    public void setBsiCore(String bsiCore) {
        this.bsiCore = bsiCore;
    }

}
