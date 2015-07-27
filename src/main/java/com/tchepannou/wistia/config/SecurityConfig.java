package com.tchepannou.wistia.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //-- Attributes
    private static final String[] ACTUATOR_ENDPOINTS = new String[]{
            "/autoconfig/**"
            , "/beans/**"
            , "/configprops/**"
            , "/dump/**"
            , "/env/**"
            , "/health/**"
            , "/info/**"
            , "/metrics/**"
            , "/mappings/**"
            , "/trace/**"
    };

    //-- WebSecurityConfigurerAdapter overrides
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers(getActuatorEndpoints()).permitAll()
                .antMatchers("/docs/**").permitAll()
                /*
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                */
        ;

        configureAuthentication(http);
        configureAuthorization(http);
    }



    //-- Beans
    @Bean
    public FilterRegistrationBean corsFilterRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain
                    filterChain)
                    throws ServletException, IOException {
                httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        });

        return registrationBean;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
                    throws IOException, ServletException {

            }
        };
    }

    //-- Protected
    protected void configureAuthentication(final HttpSecurity http) {

    }

    protected void configureAuthorization(final HttpSecurity http) {

    }

    protected String[] getActuatorEndpoints(){
        return ACTUATOR_ENDPOINTS;
    }
}
