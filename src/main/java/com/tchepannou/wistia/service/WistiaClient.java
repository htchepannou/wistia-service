package com.tchepannou.wistia.service;

import com.tchepannou.wistia.exception.WistiaException;
import com.tchepannou.wistia.model.Video;

import java.io.IOException;

public interface WistiaClient {
    Video upload (String id, String url, String projectHashedId) throws WistiaException, IOException;


}
