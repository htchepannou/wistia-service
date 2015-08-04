package com.tchepannou.wistia.service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public interface Http {
    <T> T get (URI url, Class<T> type) throws IOException;

    void delete (URI url) throws IOException;

    <T> T post(URI url, Map<String, String> params, Class<T> type) throws IOException;

    <T> T postJson(URI url, Map<String, String> params, Class<T> type) throws IOException;
}
