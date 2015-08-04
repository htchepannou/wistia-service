package com.tchepannou.wistia.service.health;

import com.tchepannou.wistia.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class CallbackHeathIndicator implements HealthIndicator{
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHeathIndicator.class);

    @Value("${callback.hostname}")
    private String hostname;

    @Value("${callback.port}")
    private int port;

    @Autowired
    private Http http;

    @Override
    public Health health() {
        String url = String.format("http://%s:%d/health", hostname, port);
        try{
            Map result = http.get(new URI(url), Map.class);
            if ("UP".equals(result.get("status"))){
                return Health
                        .up()
                        .withDetail("url", url)
                        .build();
            } else {
                return Health
                        .down()
                        .withDetail("url", url)
                        .build();
            }
        } catch (IOException | URISyntaxException e) {
            LOG.error("Connection error to {}", url, e);
            return Health
                    .down()
                    .withDetail("url", url)
                    .build();
        }
    }
}
