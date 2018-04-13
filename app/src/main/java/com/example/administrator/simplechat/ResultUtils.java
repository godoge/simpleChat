package com.example.administrator.simplechat;

import com.alibaba.fastjson.JSON;

/**
 * Created by Administrator on 2018/4/1.
 */

public class ResultUtils {
    public static String getResult(int type, int code, String msg) {
        Result r = new Result();
        r.setType(type);
        r.setMsg(msg);
        r.setCode(code);
        return JSON.toJSONString(r);
    }
}
