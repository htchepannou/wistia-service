package com.tchepannou.wistia.controller;

import com.tchepannou.wistia.Starter;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
public class HealthCheckIT {

    @Test
    public void test_status (){

        // @formatter:off
        when()
            .get("/health")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .log()
                .all()
            .body("status", is("UP"))
        ;
        // @formatter:on
    }
}
