package com.tchepannou.wistia.service.health;

import com.tchepannou.wistia.service.Http;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CallbackHeathIndicatorTest {
    //-- Attributes
    @Mock
    private Http http;

    @InjectMocks
    private HealthIndicator sensor = new CallbackHeathIndicator();

    //-- Test
    @Test
    public void testHealth_Up() throws Exception {
        // Given
        when(http.get(anyString(), anyObject())).thenReturn(Collections.singletonMap("status", "UP"));

        // When
        Health result = sensor.health();

        // Then
        assertThat(result.getStatus()).isEqualTo(Status.UP);

    }

    @Test
    public void testHealth_CallbackDown() throws Exception {
        // Given
        when(http.get(anyString(), anyObject())).thenReturn(Collections.singletonMap("status", "DOWN"));

        // When
        Health result = sensor.health();

        // Then
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);

    }

    @Test
    public void testHealth_IOException() throws Exception {
        // Given
        when(http.get(anyString(), anyObject())).thenThrow(new IOException("error"));

        // When
        Health result = sensor.health();

        // Then
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);

    }
}
