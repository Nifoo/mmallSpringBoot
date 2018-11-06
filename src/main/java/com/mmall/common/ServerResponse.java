package com.mmall.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;
    public ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isSucc(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
}
