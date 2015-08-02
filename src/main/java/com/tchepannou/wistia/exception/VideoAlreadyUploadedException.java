package com.tchepannou.wistia.exception;

public class VideoAlreadyUploadedException extends WistiaException {
    public VideoAlreadyUploadedException(String message) {
        super(message);
    }
}
