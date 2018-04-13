package com.example.administrator.simplechat;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketManage {
    private List<SocketServer> sockets = new ArrayList<>();

    private SocketManage() {

    }

    private void sendMsg(SocketServer socket, String msg) {
        for (SocketServer s : sockets) {
            if (socket == s) {
                continue;
            }
            s.sendMsg(msg);

        }
    }

    public void addSocket(Socket socket) {
        SocketServer server = new SocketServer(socket);
        server.initSocket(new SocketServer.Listener() {

            @Override
            public void onDisconnect(SocketServer server) {
                System.out.println(server);
                sockets.remove(server);

            }

            @Override
            public void onReceieMsg(SocketServer server, String msg) {
                System.out.println("收到一条消息:" + msg);
                sendMsg(server, ResultUtils.getResult(2, 1, msg));
            }
        });
        sockets.add(server);

    }

    public void removeSocket(Socket socket) {
        sockets.remove(socket);
    }

    private static SocketManage instance = new SocketManage();

    public static SocketManage getInstance() {
        return instance;

    }
}
