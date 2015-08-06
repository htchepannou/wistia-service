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
import java.util.List;

public class WistiaHealthIndicator implements HealthIndicator {
    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHeathIndicator.class);

    @Value("${wistia.api_password}")
    private String apiPassword;

    @Autowired
    private Http http;

    //-- Constructor
    public WistiaHealthIndicator (){

    }
    public WistiaHealthIndicator (String apiPassword){
        this.apiPassword = apiPassword;
    }


    //-- HealthIndicator overrides
    @Override
    public Health health() {
        String url = String.format("https://api.wistia.com/v1/projects.json?api_password=%s", apiPassword);   // NOSONAR
        String displayUrl = url.replace(apiPassword, "...");
        try{
            http.get(new URI(url), List.class);
            return Health
                    .up()
                    .withDetail("url", displayUrl)
                    .build();
        } catch (IOException | URISyntaxException e) {
            LOG.error("Connection error to {}", url, e);
            return Health
                    .down()
                    .withDetail("url", displayUrl)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
