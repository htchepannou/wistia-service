package com.tchepannou.wistia.config;

import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.HashGenerator;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import com.tchepannou.wistia.service.health.CallbackHeathIndicator;
import com.tchepannou.wistia.service.health.WistiaHealthIndicator;
import com.tchepannou.wistia.service.impl.CallbackImpl;
import com.tchepannou.wistia.service.impl.HashGeneratorImpl;
import com.tchepannou.wistia.service.impl.HttpImpl;
import com.tchepannou.wistia.service.impl.WistiaClientImpl;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.Clock;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Bean
    public Callback callback (){
        return new CallbackImpl();
    }

    @Bean
    public WistiaClient wistiaClient (){
        return new WistiaClientImpl();
    }

    @Bean
    public Http http () {
        return new HttpImpl();
    }

    @Bean
    public HashGenerator hashGenerator (){
        return new HashGeneratorImpl();
    }

    @Bean
    public Clock clock (){
        return Clock.systemUTC();
    }

    @Bean
    public HealthIndicator callbackHealthIndicator () throws IOException{
        return new CallbackHeathIndicator();
    }

    @Bean
    public HealthIndicator wistiaHealthIndicator () throws IOException{
        return new WistiaHealthIndicator();
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void resendCallbacks () throws IOException{
        callback().resend();
    }
}
