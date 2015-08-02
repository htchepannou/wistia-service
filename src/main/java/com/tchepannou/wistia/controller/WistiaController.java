package com.tchepannou.wistia.controller;

import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.exception.VideoAlreadyUploadedException;
import com.tchepannou.wistia.exception.WistiaException;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.WistiaClient;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static  final Logger LOG = LoggerFactory.getLogger(WistiaController.class);

    @Autowired
    private WistiaClient client;

    @Autowired
    private Callback callback;


    //-- Public
    @RequestMapping(method = RequestMethod.PUT, value = "/video")
    @ApiOperation("Upload a Video")
    public ResponseEntity<Video> uploadVideo (@Valid @RequestBody UploadVideoRequest request) throws IOException, WistiaException {
        try {
            Video video = client.upload(request.getId(), request.getUrl(), request.getProjectHashId());
            callback.videoUploaded(request.getId(), video);

            return new ResponseEntity<>(video, HttpStatus.CREATED);
        } catch (VideoAlreadyUploadedException e) {
            LOG.warn("Video{" + request.getUrl() + "} already uploaded", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
