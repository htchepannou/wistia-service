package com.tchepannou.wistia.config;

import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.Db;
import com.tchepannou.wistia.service.HashGenerator;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import com.tchepannou.wistia.service.impl.CallbackImpl;
import com.tchepannou.wistia.service.impl.DbImpl;
import com.tchepannou.wistia.service.impl.HashGeneratorImpl;
import com.tchepannou.wistia.service.impl.HttpImpl;
import com.tchepannou.wistia.service.impl.WistiaClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Clock;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Value("${db.directory}")
    private String dbDirectory;

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
    public Db videoDb () {
        return new DbImpl(new File(dbDirectory));
    }
}
