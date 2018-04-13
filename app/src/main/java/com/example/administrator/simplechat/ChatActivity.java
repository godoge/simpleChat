package com.example.administrator.simplechat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2018/4/1.
 */

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity implements ChatClient.ChatListener {

    private TextView addressTv;
    private RecyclerView rv;
    private ChatClient chatClient;
    private EditText inputEdt;
    private int connectState = -1;
    private Button conncetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(this));

    }

    public void onSend(View view) {
        String msg = inputEdt.getText().toString().trim();
        if (msg.isEmpty())
            return;
        if (chatClient != null) {
            chatClient.sendMsg(msg);
            inputEdt.getText().clear();
            ((MyAdapter) rv.getAdapter()).add(new Chat("我", msg, true));
            rv.getAdapter().notifyDataSetChanged();
            rv.scrollToPosition((rv.getAdapter()).getItemCount() - 1);
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView contentTv;

        public Holder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.tv_content);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<Holder> {
        private static final int TYPE_SELF = 1;
        private static final int TYPE_OTHER = 2;
        private List<Chat> list = new ArrayList<>();

        public MyAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).isSelf() ? TYPE_SELF : TYPE_OTHER;
        }

        public void add(Chat chat) {
            list.add(chat);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_SELF)
                return new Holder(getLayoutInflater().inflate(R.layout.item_right_dialog, parent, false));
            else {
                return new Holder(getLayoutInflater().inflate(R.layout.item_left_dialog, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.contentTv.setText(list.get(position).getContent());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }

    private void initView() {
        addressTv = findViewById(R.id.address);
        rv = findViewById(R.id.rv);
        inputEdt = findViewById(R.id.msg_edt_input);
        conncetBtn = findViewById(R.id.conncet);
    }


    public void onConnect(View view) {
        if (connectState == 1) {
            chatClient.close();
            return;
        } else if (connectState == 0) {
            return;
        }
        connectState = 0;
        String address = addressTv.getText().toString();
        String[] addressInfo = address.split(":");
        if (addressInfo.length != 2) {
            Toast.makeText(this, "输入有误", Toast.LENGTH_SHORT).show();
            return;
        }
        int port;
        try {
            port = Integer.parseInt(addressInfo[1]);
        } catch (Exception e) {
            Toast.makeText(this, "输入有误", Toast.LENGTH_SHORT).show();
            return;
        }
        chatClient = new ChatClient(this, addressInfo[0], port);
        chatClient.setChatListener(this);
        Toast.makeText(this, "正在连接...(" + addressInfo[0] + ":" + port + ")", Toast.LENGTH_SHORT).show();
        chatClient.connectServer();


    }

    @Override
    public void onReceivedMsg(String msg) {
        ((MyAdapter) rv.getAdapter()).add(new Chat("昵稱", msg, false));
        rv.getAdapter().notifyDataSetChanged();
        rv.scrollToPosition((rv.getAdapter()).getItemCount() - 1);
    }

    @Override
    public void onDisconnect() {
        connectState = -1;
        conncetBtn.setText("connect");
        Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected() {
        connectState = 1;
        conncetBtn.setText("disconnect");
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
        chatClient.receiveMsg();
    }

    @Override
    public void onConnectFail(String msg) {

    }

    @Override
    public void onMsgSendSuccess() {

    }


    public void onEdit(View view) {
        final EditText editText = new EditText(this);
        editText.setText(addressTv.getText());
        new AlertDialog.Builder(this).setTitle("输入服务器地址").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addressTv.setText(editText.getText());

            }
        }).setView(editText).show();
    }
}
