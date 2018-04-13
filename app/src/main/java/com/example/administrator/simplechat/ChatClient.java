package com.example.administrator.simplechat;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2018/3/31.
 */

public class ChatClient {
    private Context context;
    private String ip;
    private int port;
    private ChatListener listener;
    private Socket socket;
    private InputStream in;

    public ChatClient(Context context, String ip, int port) {
        this.context = context;
        this.ip = ip;
        this.port = port;
    }

    public void close() {

        SimpleAsyncHandle.newBuilder(context).start(new SimpleAsyncHandle.OnGetDataListener() {
            @Override
            public int onNetworkHandle(SimpleAsyncHandle.DataBundle bundle) {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                        return 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public void onFinished(int actionCode, SimpleAsyncHandle.DataBundle bundle, Context context) {
                if (actionCode == 1) {
                    listener.onDisconnect();
                }
            }
        });
    }

    public void sendMsg(final String msg) {
        SimpleAsyncHandle.newBuilder(context).start(new SimpleAsyncHandle.OnGetDataListener() {
            @Override
            public int onNetworkHandle(SimpleAsyncHandle.DataBundle bundle) {
                try {
                    socket.getOutputStream().write(msg.getBytes("utf-8"));
                    socket.getOutputStream().flush();
                    return 1;
                } catch (IOException e) {
                    bundle.putString(e.getMessage());
                    return 0;
                }

            }

            @Override
            public void onFinished(int actionCode, SimpleAsyncHandle.DataBundle bundle, Context context) {
                if (actionCode == 1) {
                    listener.onMsgSendSuccess();
                } else if (actionCode == 0) {
                    if (socket == null || socket.isClosed())
                        listener.onDisconnect();
                }
            }
        });
    }

    public void receiveMsg() {

        SimpleAsyncHandle.newBuilder(context).start(new SimpleAsyncHandle.OnGetDataListener() {
            @Override
            public void onPushed(int actionCode, Object object, Context context) {
                if (actionCode == 1) {
                    Result result = ((Result) object);
                    if (result.getType() == 2)
                        listener.onReceivedMsg(result.getMsg());
                }
            }

            @Override
            public void onFinished(int actionCode, SimpleAsyncHandle.DataBundle bundle, Context context) {
                listener.onDisconnect();
            }

            @Override
            public int onNetworkHandle(SimpleAsyncHandle.DataBundle bundle) {


                byte[] bytes = new byte[8192];
                while (true) {

                    try {
                        int len = in.read(bytes);
                        if (len == -1 && socket.isClosed())
                            break;
                        try {
                            Result r = JSON.parseObject(new String(bytes, 0, len, "utf-8"), Result.class);
                            push(1, r);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (socket.isClosed())
                            break;
                    }

                }
                return 0;
            }
        });
    }

    public void connectServer() {
        SimpleAsyncHandle.newBuilder(context).start(new SimpleAsyncHandle.OnGetDataListener() {


            @Override
            public void onFinished(int actionCode, SimpleAsyncHandle.DataBundle bundle, Context context) {
                super.onFinished(actionCode, bundle, context);
                if (actionCode == 1) {
                    Result result = (Result) bundle.getObj();
                    if (result.getType() == 1 && result.getCode() == 1) {
                        listener.onConnected();
                    }
                } else if (actionCode == 0) {
                    listener.onConnectFail(bundle.getString());
                }
            }

            @Override
            public int onNetworkHandle(SimpleAsyncHandle.DataBundle bundle) {
                try {
                    socket = new Socket(ip, port);
                    push(0, null);
                    in = socket.getInputStream();
                    byte[] bytes = new byte[2048];
                    int len = in.read(bytes);
                    String msg = new String(bytes, 0, len, "utf-8");
                    Result result = JSON.parseObject(msg, Result.class);
                    bundle.putObj(result);
                    return 1;
                } catch (Exception e) {
                    bundle.putString(e.getMessage());
                    return 0;
                }

            }
        });
    }

    public void setChatListener(ChatListener listener) {
        this.listener = listener;
    }

    public interface ChatListener {
        void onReceivedMsg(String msg);

        void onDisconnect();

        void onConnected();

        void onConnectFail(String msg);

        void onMsgSendSuccess();

    }


}
