package com.tchepannou.wistia.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import com.tchepannou.wistia.service.GreetingService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GreetingServiceImplTest {

    @InjectMocks
    private GreetingService service = new GreetingServiceImpl();

    @Test
    public void testSay() throws Exception {
        assertThat(service.say("hello")).isEqualTo("hello");
    }
}
