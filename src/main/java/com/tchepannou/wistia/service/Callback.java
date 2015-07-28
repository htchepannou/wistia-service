package com.tchepannou.wistia.service;

import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;

public interface Callback {
    void projectCreated (String id, Project project);

    void videoUploaded (String id, Video video);
}
