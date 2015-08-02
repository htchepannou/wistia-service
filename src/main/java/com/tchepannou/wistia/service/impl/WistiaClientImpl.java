package com.tchepannou.wistia.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.wistia.exception.VideoAlreadyUploadedException;
import com.tchepannou.wistia.exception.WistiaException;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Db;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WistiaClientImpl implements WistiaClient {
    //-- Attributes
    public static final String METRIC_CALLS = "wistia.calls";
    public static final String METRIC_ERRORS = "wistia.errors";
    public static final String METRIC_DURATION = "wistia.duration";


    @Value("${wistia.api_password}")
    private String apiPassword;

    @Autowired
    private Http http;

    @Autowired
    private MetricRegistry metrics;

    @Autowired
    private Db db;

    public WistiaClientImpl (){
    }

    protected WistiaClientImpl(String apiPassword){
        this.apiPassword = apiPassword;
    }

    //-- WistiaClient overrides
    @Override
    public Video upload(String id, String url, String projectHashedId) throws WistiaException, IOException {
        if (alreadyUploaded(id, url)) {
            throw new VideoAlreadyUploadedException(Arrays.asList(id, url).toString());
        } else {
            Map<String, String> params = createParams();
            params.put("url", url);
            params.put("project_id", projectHashedId);

            Video video = post("https://upload.wistia.com", params, Video.class);
            db.put(id, url);

            return video;
        }
    }


    private boolean alreadyUploaded(String id, String url) throws IOException{
        String value = db.get(id);
        return value != null && value.equalsIgnoreCase(url);
    }

    private <T> T post (String url, Map<String, String> params, Class<T> type) throws IOException {
        Timer.Context timer = metrics.timer(METRIC_DURATION).time();
        metrics.counter(METRIC_CALLS).inc();
        try {

            return http.post(url, params, type);

        } catch (IOException e){
            metrics.counter(METRIC_ERRORS).inc();
            throw e;
        } catch (RuntimeException e){
            metrics.counter(METRIC_ERRORS).inc();
            throw e;
        } finally {
            timer.stop();
        }
    }

    private Map<String, String> createParams(){
        Map<String, String> params = new HashMap<>();
        params.put("api_password", apiPassword);
        return params;
    }
}
