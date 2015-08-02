package com.tchepannou.wistia.service;

import java.io.IOException;
import java.util.Map;

public interface Http {
    <T> T get (String url, Class<T> type) throws IOException;

    void delete (String url) throws IOException;

    <T> T post(String url, Map<String, String> params, Class<T> type) throws IOException;

    <T> T postJson(String url, Map<String, String> params, Class<T> type) throws IOException;
}
