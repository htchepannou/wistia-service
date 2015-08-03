package com.tchepannou.wistia.dto;

public class CallbackResponse {
    private String status;

    public CallbackResponse(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
