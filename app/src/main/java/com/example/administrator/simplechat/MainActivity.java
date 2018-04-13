package com.example.administrator.simplechat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText portEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        portEdt = findViewById(R.id.edt_port);
    }

    public void onStartSocket(View view) {
        int port;
        try {
            port = Integer.valueOf(portEdt.getText().toString());
        } catch (Exception e) {
            return;
        }
        SimpleAsyncHandle.newBuilder(this).sendValue(port).start(new SimpleAsyncHandle.OnGetDataListener() {
            @Override
            public void onPushed(int actionCode, Object object, Context context) {
                Toast.makeText(context, "服务已开启", Toast.LENGTH_SHORT).show();
            }

            @Override
            public int onNetworkHandle(SimpleAsyncHandle.DataBundle bundle) {

                try {
                    ServerSocket serverSocket = new ServerSocket(receiveValue(0, Integer.class));
                    push(1, null);
                    while (true) {

                        Socket socket = serverSocket.accept();
                        SocketManage.getInstance().addSocket(socket);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void goToChatRoom(View view) {
        startActivity(new Intent(this, ChatActivity.class));
    }
}
