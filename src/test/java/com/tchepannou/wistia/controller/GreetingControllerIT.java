package com.tchepannou.wistia.controller;

import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.tchepannou.wistia.Starter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
public class GreetingControllerIT {
    @Value("${server.port}")
    int port;

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void testGet (){
        // @formatter:off
        given ()
            .param("name", "hello")
        .when()
            .get("/api/greeting")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .log()
                .all()
            .body("content", is("hello"))
        ;
        // @formatter:on
    }
}
