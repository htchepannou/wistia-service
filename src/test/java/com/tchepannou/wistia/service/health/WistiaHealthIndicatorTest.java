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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WistiaHealthIndicatorTest {
    //-- Attributes
    @Mock
    private Http http;

    @InjectMocks
    private HealthIndicator sensor = new WistiaHealthIndicator("_project_hash_id_", "_password_");

    //-- Test
    @Test
    public void testHealth_Up() throws Exception {
        // Given
        when(http.get(any(URI.class), anyObject())).thenReturn(new HashMap<>());

        // When
        Health result = sensor.health();

        // Then
        verify(http).get(new URI("https://api.wistia.com/v1/projects/_project_hash_id_.json?api_password=_password_"), Map.class);

        assertThat(result.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void testHealth_Down() throws Exception {
        // Given
        when(http.get(any(URI.class), anyObject())).thenThrow(new IOException ("foo"));

        // When
        Health result = sensor.health();

        // Then
        verify(http).get(new URI("https://api.wistia.com/v1/projects/_project_hash_id_.json?api_password=_password_"), Map.class);

        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
    }
}
