package com.tchepannou.wistia.exception;

import java.io.IOException;

public class WistiaException extends IOException {
    public WistiaException(String message) {
        super(message);
    }
}
