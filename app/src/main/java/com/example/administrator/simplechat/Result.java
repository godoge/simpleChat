package com.example.administrator.simplechat;

/**
 * Created by Administrator on 2018/4/1.
 */

public class Result {
    //1.连接 2.聊天
    private int type;
    private String msg;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
