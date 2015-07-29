package com.tchepannou.wistia.service.impl;

import com.google.common.base.Joiner;
import com.tchepannou.wistia.service.HashGenerator;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class HashGeneratorImpl implements HashGenerator {
    @Override
    public String generate(String apiKey, Collection<String> values) {
        try {
            String str = Joiner
                    .on("-")
                    .skipNulls()
                    .join(Joiner.on("").join(values), apiKey);
            return DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e){
            throw new IllegalStateException("Unable to generate the hash", e);
        }
    }
}
