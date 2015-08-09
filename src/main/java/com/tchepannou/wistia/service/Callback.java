package com.tchepannou.wistia.service;

import com.tchepannou.wistia.model.Video;

import java.io.IOException;

public interface Callback {
    void videoUploaded (String id, Video video);

    void resend() throws IOException;
}
