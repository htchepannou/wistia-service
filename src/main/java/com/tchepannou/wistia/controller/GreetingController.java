package com.tchepannou.wistia.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tchepannou.wistia.dto.GreetingDto;
import com.tchepannou.wistia.service.GreetingService;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Api(basePath = "/greeting", value = "Greeting", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value="/api/greeting", produces = MediaType.APPLICATION_JSON_VALUE)
public class GreetingController {
    // Attributes
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private GreetingService greetingService;

    //-- REST methods
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("Sample Method")
    public GreetingDto greeting(@RequestParam(value="name", defaultValue="World") String name) {
        final String word = greetingService.say(name);
        return new GreetingDto(counter.incrementAndGet(), word);
    }
}
