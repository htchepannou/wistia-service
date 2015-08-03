package com.tchepannou.wistia.service.health;

import com.tchepannou.wistia.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.io.IOException;
import java.util.Map;

public class WistiaHealthIndicator implements HealthIndicator {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHeathIndicator.class);

    @Value("${wistia.test_project_hashed_id}")
    private String projectHashId;

    @Value("${wistia.api_password}")
    private String apiPassword;

    @Autowired
    private Http http;

    @Override
    public Health health() {
        String url = String.format("https://api.wistia.com/v1/projects/%s.json?api_password=%s", projectHashId, apiPassword);
        String displayUrl = url.replace(apiPassword, "...");
        try{
            http.get(url, Map.class);
            return Health
                    .up()
                    .withDetail("url", displayUrl)
                    .build();
        } catch (IOException e) {
            LOG.error("Connection error to {}", url, e);
            return Health
                    .down()
                    .withDetail("url", displayUrl)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
