package com.tchepannou.wistia.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectTest {
    @Test
    public void testJson() throws Exception {
        // Given
        String json = "{\n"
                + "  \"id\": 1,\n"
                + "  \"name\": \"Jeff's First Project\",\n"
                + "  \"description\": \"This Project needs a description BAD.\",\n"
                + "  \"mediaCount\": 5,\n"
                + "  \"created\": \"2013-08-15T18:47:39+00:00\",\n"
                + "  \"updated\": \"2013-08-15T18:47:39+00:00\",\n"
                + "  \"hashedId\": \"lpzgy6e09m\",\n"
                + "  \"anonymousCanUpload\": false,\n"
                + "  \"anonymousCanDownload\": false,\n"
                + "  \"public\": false,\n"
                + "  \"publicId\": \"lpzgy6e09m\"\n"
                + "}";

        // When
        Project project = new ObjectMapper().readValue(json, Project.class);

        // Then
        assertThat(project.getId()).isEqualTo(1);
        assertThat(project.getName()).isEqualTo("Jeff's First Project");
        assertThat(project.getCreated()).isNotNull();
        assertThat(project.getUpdated()).isNotNull();
        assertThat(project.getHashedId()).isEqualTo("lpzgy6e09m");
        assertThat(project.getMediaCount()).isEqualTo(5);
    }
}
