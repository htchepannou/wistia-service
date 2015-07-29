package com.tchepannou.wistia.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VideoTest {

    @Test
    public void testJson() throws Exception {
        // Given
        String json = "{\n"
                + "    \"id\": 15151433,\n"
                + "    \"name\": \"627829_1354730811702_preview.mov\",\n"
                + "    \"type\": \"Video\",\n"
                + "    \"created\": \"2015-07-29T06:58:37+00:00\",\n"
                + "    \"updated\": \"2015-07-29T06:58:37+00:00\",\n"
                + "    \"hashed_id\": \"z4tcp18a38\",\n"
                + "    \"description\": \"\",\n"
                + "    \"progress\": 1,\n"
                + "    \"status\": \"queued\",\n"
                + "    \"thumbnail\": {\n"
                + "        \"url\": \"https://fast.wistia.com/assets/images/zebra/elements/dashed-thumbnail.png\",\n"
                + "        \"width\": 100,\n"
                + "        \"height\": 60\n"
                + "    },\n"
                + "    \"account_id\": 358127\n"
                + "}";

        // When
        Video video = new ObjectMapper().readValue(json, Video.class);

        // Then
        assertThat(video.getId()).isEqualTo(15151433);
        assertThat(video.getName()).isEqualTo("627829_1354730811702_preview.mov");
        assertThat(video.getCreated()).isNotNull();
        assertThat(video.getUpdated()).isNotNull();
        assertThat(video.getHashedId()).isEqualTo("z4tcp18a38");
        assertThat(video.getThumbnail().getUrl()).isEqualTo("https://fast.wistia.com/assets/images/zebra/elements/dashed-thumbnail.png");
        assertThat(video.getThumbnail().getWidth()).isEqualTo(100);
        assertThat(video.getThumbnail().getHeight()).isEqualTo(60);
    }
}
