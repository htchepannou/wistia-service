package com.tchepannou.wistia.service;

import java.io.IOException;
import java.util.Map;

public interface Http {
    <T> T post(String url, Map<String, String> params, Class<T> type) throws IOException;
}
