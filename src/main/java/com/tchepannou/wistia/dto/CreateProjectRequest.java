package com.tchepannou.wistia.dto;

import org.hibernate.validator.constraints.NotBlank;

public class CreateProjectRequest {
    @NotBlank(message = "id")
    private String id;

    @NotBlank(message = "name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
