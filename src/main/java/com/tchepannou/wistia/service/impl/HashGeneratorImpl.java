package com.tchepannou.wistia.service.impl;

import com.google.common.base.Joiner;
import com.tchepannou.wistia.service.HashGenerator;
import org.springframework.util.DigestUtils;

import java.util.Collection;

public class HashGeneratorImpl implements HashGenerator {
    @Override
    public String generate(String apiKey, Collection<String> values) {
        String str = Joiner
                .on("")
                .skipNulls()
                .join(values);
        return DigestUtils.md5DigestAsHex((str + "-" + apiKey).getBytes());
    }
}
