package com.tchepannou.wistia.controller;

import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.WistiaClient;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Api(basePath = "/wistia", value = "Bridge to Wistia", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value="/api/wistia", produces = MediaType.APPLICATION_JSON_VALUE)
public class WistiaController {
    //-- Attributes
    @Autowired
    private WistiaClient client;

    @Autowired
    private Callback callback;


    //-- Public
    @RequestMapping(method = RequestMethod.PUT, value = "/video")
    @ApiOperation("Upload a Video")
    public ResponseEntity<Video> uploadVideo (@Valid @RequestBody UploadVideoRequest request) throws IOException {
        Video video = client.upload(request.getUrl(), request.getHashId(), request.getProjectHashId());
        if (!video.getHashedId().equals(request.getHashId())) {
            callback.videoUploaded(request.getId(), video);
        }

        return new ResponseEntity<>(video, HttpStatus.CREATED);
    }
}
