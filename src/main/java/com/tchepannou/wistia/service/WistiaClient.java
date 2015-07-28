package com.tchepannou.wistia.service;

import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;

import java.io.IOException;

public interface WistiaClient {
    Project createProject(String name) throws IOException;

    Video upload (String url, String projectHashedId) throws IOException;


}
