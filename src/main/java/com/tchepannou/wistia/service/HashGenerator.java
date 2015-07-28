package com.tchepannou.wistia.service;

import java.util.Collection;

public interface HashGenerator {
    String generate(String apiKey, Collection<String> values);
}
