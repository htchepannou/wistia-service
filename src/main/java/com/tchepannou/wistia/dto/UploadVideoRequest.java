package com.tchepannou.wistia.dto;

import org.hibernate.validator.constraints.NotBlank;

public class UploadVideoRequest {
    //-- Attributes
    @NotBlank(message = "id")
    private String id;

    @NotBlank(message = "url")
    private String url;

    private String projectHashId;

    private String hashId;


    //-- Getter/Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectHashId() {
        return projectHashId;
    }

    public void setProjectHashId(String projectHashId) {
        this.projectHashId = projectHashId;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }
}
