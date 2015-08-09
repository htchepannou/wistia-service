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
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WistiaHealthIndicatorTest {
    //-- Attributes
    @Mock
    private Http http;

    @InjectMocks
    private HealthIndicator sensor = new WistiaHealthIndicator("_password_");

    //-- Test
    @Test
    public void testHealth_Up() throws Exception {
        // Given
        when(http.get(anyString(), anyObject())).thenReturn(new HashMap<>());

        // When
        Health result = sensor.health();

        // Then
        verify(http).get("https://api.wistia.com/v1/projects.json?api_password=_password_", List.class);

        assertThat(result.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void testHealth_Down() throws Exception {
        // Given
        when(http.get(anyString(), anyObject())).thenThrow(new IOException ("foo"));

        // When
        Health result = sensor.health();

        // Then
        verify(http).get("https://api.wistia.com/v1/projects.json?api_password=_password_", List.class);

        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
    }
}
