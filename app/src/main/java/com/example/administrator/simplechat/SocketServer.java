package com.example.administrator.simplechat;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class SocketServer {
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private Listener listener;

    public SocketServer(Socket socket) {
        this.socket = socket;

    }

    public void initSocket(Listener listener) {

        this.listener = listener;
        try {
            out = socket.getOutputStream();
            out.write(ResultUtils.getResult(1, 1, "已连接到服务器!").getBytes("utf-8"));
            out.flush();
            in = socket.getInputStream();
            receiveMsg();
        } catch (IOException e) {
            e.printStackTrace();
            if (socket.isClosed()) {
                listener.onDisconnect(this);
            }

        }

    }

    public Socket getSocket() {
        return socket;
    }

    public static interface Listener {
        void onDisconnect(SocketServer server);

        void onReceieMsg(SocketServer server, String msg);
    }

    public void sendMsg(String mString) {

        try {
            out.write(mString.getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void receiveMsg() {
        new Thread(new Runnable() {

            @Override
            public void run() {


                byte[] bytes = new byte[8192];
                while (true) {

                    try {
                        int len = in.read(bytes);
                        if (len == -1 && socket.isClosed()) {
                            listener.onDisconnect(SocketServer.this);
                            break;
                        }
                        listener.onReceieMsg(SocketServer.this, new String(bytes, 0, len, "utf-8"));
                    } catch (IOException e) {

                        if (socket.isClosed()) {
                            listener.onDisconnect(SocketServer.this);
                            break;
                        }
                    }
                }


            }
        }).start();

    }
}
