package com.example.administrator.simplechat;

/**
 * Created by Administrator on 2018/4/1.
 */

public class Chat {
    private String name;
    private String content;
    private boolean isSelf;

    public Chat(String name, String content, boolean isSelf) {
        this.name = name;
        this.content = content;
        this.isSelf = isSelf;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isSelf() {
        return isSelf;
    }
}
