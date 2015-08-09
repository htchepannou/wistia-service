package com.tchepannou.wistia.service.impl;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class HashGeneratorImplTest {

    @Test
    public void testGenerate() throws Exception {
        String result = new HashGeneratorImpl().generate("foo", Arrays.asList("ray", "sponsible"));

        assertThat(result).isEqualTo("15eca3878dc5eba13729a48d0cb2db9a");
    }

    @Test(expected = IllegalStateException.class)
    public void testGenerate_BadEncoding() throws Exception {
        new HashGeneratorImpl("???").generate("foo", Arrays.asList("ray", "sponsible"));
    }
}
