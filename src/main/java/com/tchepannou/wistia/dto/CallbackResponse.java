package com.tchepannou.wistia.dto;

public class CallbackResponse {
    private String status;

    public CallbackResponse(){
    }
    public CallbackResponse(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
