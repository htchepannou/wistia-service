package com.tchepannou.wistia.service;

import java.io.IOException;

public interface Db {
    void put(String id, String value) throws IOException;
    boolean remove (String id);
    String get(String id) throws IOException;
}
