package com.qiao;

import jline.console.ConsoleReader;
import jline.console.KeyMap;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BSIConsole {
    private static final byte EOT = 0x04;
    private static final byte EOF = -1;

    private final ConsoleReader console;
    private final Socket socket;
    private BufferedWriter socketWriter;
    private BufferedReader socketReader;
    private volatile boolean isRunning;
    // 5分钟
    private static final int _1MIN = 60 * 1000;
    private final Writer out;

    public BSIConsole(InetSocketAddress address) throws IOException {
        this.console = initConsoleReader();
        this.socket = connect(address);
        this.out = this.console.getOutput();

        this.isRunning = true;
        activeConsoleReader();

        socketWriter.write("version\n");
        socketWriter.flush();
        loopForWriter();
    }

    private ConsoleReader initConsoleReader() throws IOException {
        ConsoleReader console = new ConsoleReader(System.in, System.out);
        console.getKeys().bind("" + KeyMap.CTRL_D, (ActionListener) (e) -> {
            try {
                socketWriter.write(KeyMap.CTRL_D);
                socketWriter.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return console;
    }

    /**
     * 激活读线程
     */
    private void activeConsoleReader() {
        final Thread socketThread = new Thread("bsi-console-reader-daemon") {

            private StringBuilder lineBuffer = new StringBuilder();

            @Override
            public void run() {
                try {

                    while (isRunning) {

                        final String line = console.readLine();

                        // 如果是\结尾，则说明还有下文，需要对换行做特殊处理
                        if (StringUtils.endsWith(line, "\\")) {
                            // 去掉结尾的\
                            lineBuffer.append(line.substring(0, line.length() - 1));
                            continue;
                        } else {
                            lineBuffer.append(line);
                        }

                        final String lineForWrite = lineBuffer.toString();
                        lineBuffer = new StringBuilder();

                        console.setPrompt(EMPTY);
                        if (isNotBlank(lineForWrite)) {
                            socketWriter.write(lineForWrite + "\n");
                        } else {
                            socketWriter.write("\n");
                        }
                        socketWriter.flush();

                    }
                } catch (IOException e) {
                    shutdown();
                }

            }

        };
        socketThread.setDaemon(true);
        socketThread.start();
    }


    /**
     * 激活网络
     */
    private Socket connect(InetSocketAddress address) throws IOException {
        final Socket socket = new Socket();
        socket.setSoTimeout(0);
        socket.connect(address, _1MIN);
        socket.setKeepAlive(true);
        socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return socket;
    }

    private void loopForWriter() {

        try {
            while (isRunning) {
                final int c = socketReader.read();
                if (c == EOF) {
                    break;
                }
                if (c == EOT) {
                    console.setPrompt("bsi?>");
                    console.redrawLine();
                } else {
                    out.write(c);
                }
                out.flush();
            }
        } catch (IOException e) {
//            err("write fail : %s", e.getMessage());
            shutdown();
        }

    }

    /**
     * 关闭Console
     */
    private void shutdown() {
        isRunning = false;
        closeQuietly(socketWriter);
        closeQuietly(socketReader);
        closeQuietly(socket);
        console.shutdown();
    }


}























