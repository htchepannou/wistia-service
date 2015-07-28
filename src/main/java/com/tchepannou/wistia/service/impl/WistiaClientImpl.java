package com.tchepannou.wistia.service.impl;

import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WistiaClientImpl implements WistiaClient {
    //-- Attributes
    @Autowired
    private Http http;

    @Value("${wistia.api_password}")
    private String apiPassword;

    public WistiaClientImpl (){
    }

    protected WistiaClientImpl(String apiPassword){
        this.apiPassword = apiPassword;
    }

    //-- WistiaClient overrides
    @Override
    public Project createProject(String name) throws IOException {
        Map<String, String> params = createParams();
        params.put("name", name);
        params.put("anonymousCanUpload", "0");
        params.put("anonymousCanDownload", "0");
        params.put("public", "0");

        return http.post("https://api.wistia.com/v1/projects.json", params, Project.class);
    }

    @Override
    public Video upload(String url, String projectHashedId) throws IOException {
        Map<String, String> params = createParams();
        params.put("url", url);
        params.put("project_id", projectHashedId);

        return http.post("https://upload.wistia.com", params, Video.class);
    }


    private Map<String, String> createParams(){
        Map<String, String> params = new HashMap<>();
        params.put("api_password", apiPassword);
        return params;
    }
}
