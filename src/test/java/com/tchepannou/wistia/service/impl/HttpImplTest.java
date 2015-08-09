package com.tchepannou.wistia.service.impl;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class HttpImplTest {

    @Test(expected = IOException.class)
    public void testGet_ThrowIOException_OnBadStatus() throws Exception {
        new HttpImpl().get("http://goog.ca/invalid", Map.class);
    }
}
