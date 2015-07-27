package com.tchepannou.wistia.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.tchepannou.wistia.dto.GreetingDto;
import com.tchepannou.wistia.service.GreetingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GreetingControllerTest {

    @Mock
    private GreetingService service;

    @InjectMocks
    private GreetingController controller;

    @Test
    public void testGreeting() throws Exception {
        // Given
        when(service.say("hello")).thenReturn("hello");

        // When
        GreetingDto result = controller.greeting("hello");

        // Then
        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getId()).isGreaterThan(0);
    }
}
