package com.tchepannou.wistia.service;

import com.tchepannou.wistia.model.Video;

public interface Callback {
    void videoUploaded (String id, Video video);
}
