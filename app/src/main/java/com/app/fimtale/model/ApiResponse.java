package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

/**
 * 通用 API 响应包装
 * 后端统一返回格式：{ code: 0, msg: "", data: {...} }
 */
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;
    private int duration;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
    public int getDuration() { return duration; }
}
