package com.qiao.server;

import com.qiao.Configure;
import com.qiao.util.BSICheckUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import static java.nio.channels.SelectionKey.OP_READ;

public class BSIServer {
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final byte CTRL_D = 0x04;
    private static final byte CTRL_X = 0x18;
    private static final byte EOT = 0x04;
    private static final int EOF = -1;



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


    private void activeSelectorDaemon(final Selector selector, final Configure configure) {

        final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        Thread selectorDaemon = new Thread() {
            @Override
            public void run() {
                while (!interrupted()) {
                    try {
                        if (selector.isOpen() && selector.select() > 0) {
                            final Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                            while (selectionKeyIterator.hasNext()) {
                                SelectionKey selectionKey = selectionKeyIterator.next();
                                selectionKeyIterator.remove();

                                if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                                    doAccept(selectionKey, selector, configure);
                                }
                                
                                if (selectionKey.isValid() && selectionKey.isReadable()) {
                                    doRead(byteBuffer, selectionKey);
                                }

                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        selectorDaemon.setDaemon(true);
        selectorDaemon.start();
    }

    private void doRead(ByteBuffer byteBuffer, SelectionKey selectionKey) {
    }

    private void doAccept(SelectionKey selectionKey, Selector selector, Configure configure) {
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        acceptSocketChannel(selector, serverSocketChannel, configure);
    }

    private SocketChannel acceptSocketChannel(Selector selector, ServerSocketChannel serverSocketChannel, Configure configure) throws IOException {
        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.socket().setSoTimeout(configure.getConnectTimeout());
        socketChannel.socket().setTcpNoDelay(true);


        socketChannel.register(selector, OP_READ);

        // 这里输出Logo
        writeToSocketChannel(socketChannel, Charset.forName("UTF-8"), "BSI");

        // 绘制提示符
//        writeToSocketChannel(socketChannel, session.getCharset(), session.prompt());

        // Logo结束之后输出传输中止符
        writeToSocketChannel(socketChannel, ByteBuffer.wrap(new byte[]{EOT}));

        return socketChannel;
    }

    private void writeToSocketChannel(SocketChannel socketChannel, Charset charset, String message) throws IOException {
        writeToSocketChannel(socketChannel, ByteBuffer.wrap(message.getBytes(charset)));
    }

    private void writeToSocketChannel(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }


    @Override
    public String toString() {
        return "BSIServer Test";
    }
}
