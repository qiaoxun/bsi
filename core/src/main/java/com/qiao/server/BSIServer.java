package com.qiao.server;

import com.qiao.Configure;
import com.qiao.util.BSICheckUtils;
import com.sun.deploy.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class BSIServer {
    private ServerSocketChannel serverSocketChannel = null;
    private Selector selector = null;

    public void bind(Configure configure) throws IOException {

        selector = Selector.open();

        serverSocketChannel =ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().setSoTimeout(configure.getConnectTimeout());
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.socket().bind(getInetSocketAddress(configure.getTargetIp(), configure.getTargetPort()), 24);

    }

    /*
     * 获取绑定网络地址信息<br/>
     * 这里做个小修正,如果targetIp为127.0.0.1(本地环回口)，则需要绑定所有网卡
     * 否则外部无法访问，只能通过127.0.0.1来进行了
     */
    private InetSocketAddress getInetSocketAddress(String targetIp, int targetPort) {
        if (BSICheckUtils.isEquals("127.0.0.1", targetIp)) {
            return new InetSocketAddress(targetPort);
        } else {
            return new InetSocketAddress(targetIp, targetPort);
        }
    }

    @Override
    public String toString() {
        return "BSIServer Test";
    }
}
