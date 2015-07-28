package com.tchepannou.wistia.service.impl;

import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.HashGenerator;
import com.tchepannou.wistia.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class CallbackImpl implements Callback {
    //-- Enums
    private enum Category {
        video, project
    }

    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(CallbackImpl.class);

    @Value("${callback.url}")
    private String callbackUrl;

    @Value("${callback.error_dir}")
    private String errorDir;

    @Value("${callback.api_key}")
    private String apiKey;

    @Autowired
    private Clock clock;

    @Autowired
    private Http http;

    @Autowired
    private HashGenerator hashGenerator;

    //-- Constructor
    public CallbackImpl(){
    }
    protected CallbackImpl(String callbackUrl, String errorDir, String apiKey){
        this.callbackUrl = callbackUrl;
        this.errorDir = errorDir;
        this.apiKey = apiKey;
    }

    //-- Callback override
    @Override
    public void projectCreated(String id, Project project) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("event", "project-created");
        params.put("id", id);
        params.put("hashed_id", project.getHashedId());
        post(id, params, Category.project);
    }

    @Override
    public void videoUploaded(String id, Video video) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("event", "video-uploaded");
        params.put("id", id);
        params.put("hashed_id", video.getHashedId());
        post(id, params, Category.video);
    }

    //-- Private
    private void post (String id, Map<String, String> params, Category category) {
        try {
            String hash = hashGenerator.generate(apiKey, params.values());
            params.put("x-timestamp", String.valueOf(clock.millis()));
            params.put("x-hash", hash);

            http.post(callbackUrl, params, String.class);
        } catch (IOException e) {
            LOG.error("\n---------------------\nPOST {}\n{}\n", callbackUrl, params, e);

            onError(id, params, category);
        }
    }

    private void onError (String id, Map<String, String> params, Category category){
        String filename = String.format("%d-%s-%s", clock.millis(), category.name(), id);
        File file = new File(errorDir, filename);

        file.getParentFile().mkdirs();

        try(OutputStream out = new FileOutputStream(file)){
            Properties properties = new Properties();
            properties.putAll(params);
            properties.store(out, id);
        } catch (IOException e){
            LOG.error("Unable to store error into {}\nid={}\nparams={}", file, id, params, e);
        }
    }
}
