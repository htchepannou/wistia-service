package com.tchepannou.wistia.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.wistia.dto.CallbackResponse;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.HashGenerator;
import com.tchepannou.wistia.service.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Clock;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CallbackImpl implements Callback {
    //-- Enums
    private enum Category {
        VIDEO
    }

    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(CallbackImpl.class);

    public static final String METRIC_CALLS = "wistia.callback.calls";
    public static final String METRIC_ERRORS = "wistia.callback.errors";
    public static final String METRIC_DURATION = "wistia.callback.duration";
    public static final String METRIC_SPOOL_SIZE = "wistia.callback.spool.size";

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

    @Autowired
    private MetricRegistry metrics;

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
    public void videoUploaded(String id, Video video) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("event", "video-uploaded");
        params.put("id", id);
        params.put("name", video.getName());
        params.put("hashed_id", video.getHashedId());
        post(id, params, Category.VIDEO);
    }

    @Override
    public void resend() {
        List<File> files = getErrorFiles();
        Set<String> ids = new HashSet<>();
        LOG.info("{} events to resend", files.size());

        for (File file : files){
            try(InputStream in = new FileInputStream(file)){
                /* load the data */
                Properties properties = new Properties();
                properties.load(in);

                /* sending callback */
                String id = properties.getProperty("id");
                if (ids.add(id)) {
                    Video video = new Video();
                    video.setName(properties.getProperty("name"));
                    video.setHashedId(properties.getProperty("hashed_id"));

                    LOG.info("Resending the notification for Video{}", id);
                    videoUploaded(id, video);
                }

                /* delete */
                if (file.delete()) {
                    LOG.info("Deleted: " + file);
                    metrics.counter(METRIC_SPOOL_SIZE).dec();
                } else {
                    LOG.warn("Unable to delete " + file);
                }
            } catch (IOException e){
                LOG.warn("IO error", e);
            }
        }
    }

    //-- Private
    private List<File> getErrorFiles (){
        File dir = new File(errorDir);
        File[] afiles = dir.listFiles();
        if (afiles == null){
            return Collections.emptyList();
        }

        List<File> files = Arrays.asList(afiles);
        Collections.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));
        return files;
    }

    private void post (String id, Map<String, String> params, Category category) {
        Timer.Context timer = metrics.timer(METRIC_DURATION).time();
        metrics.counter(METRIC_CALLS).inc();

        try {

            String hash = hashGenerator.generate(apiKey, params.values());
            params.put("x-timestamp", String.valueOf(clock.millis()));
            params.put("x-hash", hash);

            http.postJson(new URI(callbackUrl), params, CallbackResponse.class);

        } catch (Exception e) {

            LOG.error("FAIL POST {} - {}", callbackUrl, params, e);
            metrics.counter(METRIC_ERRORS).inc();
            onError(id, params, category);

        } finally {
            timer.stop();
        }
    }

    private void onError (String id, Map<String, String> params, Category category){
        String filename = String.format("%d-%s-%s", clock.millis(), category.name().toLowerCase(), id);
        File file = new File(errorDir, filename);

        file.getParentFile().mkdirs();

        try(OutputStream out = new FileOutputStream(file)){
            Properties properties = new Properties();
            properties.putAll(params);
            properties.store(out, id);

            metrics.counter(METRIC_SPOOL_SIZE).inc();
        } catch (IOException e){
            LOG.error("Unable to store error into {}\nid={}\nparams={}", file, id, params, e);
        }
    }
}
