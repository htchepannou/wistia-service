package com.tchepannou.wistia.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
public class SwaggerConfig {
    //-- Attributes
    private SpringSwaggerConfig springSwaggerConfig;

    @Value("${swagger.service.version}")
    private String serviceVersion;

    @Value("${swagger.service.title}")
    private String serviceTitle;

    @Value("${swagger.service.description}")
    private String serviceDescription;

    @Value("${swagger.service.termsUrl}:")
    private String serviceTermsUrl;

    @Value("${swagger.service.email}:")
    private String serviceEmail;

    @Value("${swagger.service.licenceType}:")
    private String serviceLicenceType;

    @Value("${swagger.service.licenceUrl}:")
    private String serviceLicenceUrl;

    //-- Beans
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .includePatterns("/api.*");
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                serviceTitle,
                serviceDescription,
                serviceTermsUrl,
                serviceEmail,
                serviceLicenceType,
                serviceLicenceUrl);
        return apiInfo;
    }
}
