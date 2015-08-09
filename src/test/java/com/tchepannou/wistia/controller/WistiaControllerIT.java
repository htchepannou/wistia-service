package com.tchepannou.wistia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.tchepannou.wistia.Starter;
import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.service.Http;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
public class WistiaControllerIT extends AbstractHandler {
    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(WistiaControllerIT.class);

    @Value("${server.port}")
    private int port;

    @Value("${wistia.test_project_hashed_id}")
    private String projectHashedKey;

    @Value("${wistia.api_password}")
    private String apiPassword;

    @Value("${callback.error_dir}")
    private String errorDir;

    @Autowired
    private Http http;

    private Map callbackData;

    @Value("${callback.port}")
    private int callbackPort;

    private Server callback;

    private int httpStatus;


    //-- AbstractHandler overrides
    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        LOG.info("handling request: " + request);

        httpServletResponse.addHeader("Content-Type", "application/json");
        httpServletResponse.setStatus(httpStatus);

        if (httpStatus == 200) {
            callbackData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            httpServletResponse.getWriter().write("{\"status\":\"OK\"}");
        }
        request.setHandled(true);

        FileUtils.deleteDirectory(new File(errorDir));
    }

    //-- Tests
    @Before
    public void setUp () throws Exception {
        RestAssured.port = port;

        LOG.info("Starting callback server on port:" + callbackPort);
        callback = new Server(callbackPort);
        callback.setHandler(this);
        callback.start();

        callbackData = new HashMap<>();

        httpStatus = 200;
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Stopping callback server");
        callback.stop();
    }

    @Test
    public void testUpload_Create() throws Exception {
        final String url = "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl(url);

        // @formatter:off
        String hashedId = given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_CREATED)
            .body("name", is("big_buck_bunny_720p_1mb.mp4"))
            .body("type", is("Video"))
            .body("created", notNullValue())
            .body("updated", notNullValue())
        .extract()
            .path("hashed_id")
        ;
        // @formatter:on

        /* make sure video uploaded */
        String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
        try {
            Map video = http.get(new URI(videoUrl), Map.class);
            assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
            assertThat(video.get("type")).isEqualTo("Video");
            assertThat(video.get("hashed_id")).isEqualTo(hashedId);
        } finally {
            deleteVideo(hashedId);
        }

        /* make sure callback sent */
        assertThat(callbackData).isNotNull();
        assertThat(callbackData.get("id")).isEqualTo("12345");
        assertThat(callbackData.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
        assertThat(callbackData.get("hashed_id")).isEqualTo(hashedId);
        assertThat(callbackData.get("event")).isEqualTo("video-uploaded");
        assertThat(callbackData).containsKey("x-timestamp");
        assertThat(callbackData).containsKey("x-hash");

        /* Not error */
        assertThat(new File(errorDir)).doesNotExist();
    }

    @Test
    public void testUpload_Update() throws Exception {
        /* first upload */
        final String url = "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4";
        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl(url);

        // @formatter:off
        String hashedId0 = given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("hashed_id")
        ;
        // @formatter:on

        String hashedId;
        try {
            /* update */
            request.setUrl("http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");
            request.setHashId(hashedId0);

            // @formatter:off
            hashedId = given ()
                    .contentType(ContentType.JSON)
                    .content(request, ObjectMapperType.JACKSON_2)
            .when()
                .put("/api/wistia/video")
            .then()
                .log()
                    .all()
                .statusCode(HttpStatus.SC_CREATED)
                .body("name", is("big_buck_bunny_720p_1mb.mp4"))
                .body("type", is("Video"))
                .body("created", notNullValue())
                .body("updated", notNullValue())
            .extract()
                .path("hashed_id")
            ;
            // @formatter:on

            /* make sure video updated */
            String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
            try {
                Map video = http.get(new URI(videoUrl), Map.class);
                assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
                assertThat(video.get("type")).isEqualTo("Video");
                assertThat(video.get("hashed_id")).isEqualTo(hashedId);
                assertThat(hashedId).isNotEqualTo(hashedId0);
            } finally {
                deleteVideo(hashedId);
            }
        } finally {
            deleteVideo(hashedId0);
        }


        /* make sure callback sent */
        assertThat(callbackData).isNotNull();
        assertThat(callbackData.get("id")).isEqualTo("12345");
        assertThat(callbackData.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
        assertThat(callbackData.get("hashed_id")).isEqualTo(hashedId);
        assertThat(callbackData.get("event")).isEqualTo("video-uploaded");
        assertThat(callbackData).containsKey("x-timestamp");
        assertThat(callbackData).containsKey("x-hash");

        /* Not error */
        assertThat(new File(errorDir)).doesNotExist();
    }

    @Test
    public void testUpload_Update_NoChange() throws Exception {
        /* first upload */
        final String url = "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl(url);

        // @formatter:off
        String hashedId0 = given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("hashed_id")
        ;
        // @formatter:on

        String hashedId;
        callbackData.clear();
        try {
            /* update */
            request.setUrl("http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");
            request.setHashId(hashedId0);

            // @formatter:off
            hashedId = given ()
                    .contentType(ContentType.JSON)
                    .content(request, ObjectMapperType.JACKSON_2)
            .when()
                .put("/api/wistia/video")
            .then()
                .log()
                    .all()
                .statusCode(HttpStatus.SC_CREATED)
                .body("name", is("big_buck_bunny_720p_1mb.mp4"))
                .body("type", is("Video"))
                .body("created", notNullValue())
                .body("updated", notNullValue())
            .extract()
                .path("hashed_id")
            ;
            // @formatter:on

            /* make sure video updated */
            String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
            try {
                Map video = http.get(new URI(videoUrl), Map.class);
                assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
                assertThat(video.get("type")).isEqualTo("Video");
                assertThat(video.get("hashed_id")).isEqualTo(hashedId);
                assertThat(hashedId).isEqualTo(hashedId0);
            } finally {
                deleteVideo(hashedId);
            }
        } finally {
            deleteVideo(hashedId0);
        }

        /* make sure callback sent */
        assertThat(callbackData).isEmpty();

        /* Not error */
        assertThat(new File(errorDir)).doesNotExist();
    }

    @Test
    public void testUpload_Callback_NotFound() throws Exception {
        this.httpStatus = 404;

        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl("http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");

        // @formatter:off
        String hashedId = given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_CREATED)
            .body("name", is("big_buck_bunny_720p_1mb.mp4"))
            .body("type", is("Video"))
            .body("created", notNullValue())
            .body("updated", notNullValue())
        .extract()
            .path("hashed_id")
        ;
        // @formatter:on

        /* make sure video updated */
        String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
        try {
            Map video = http.get(new URI(videoUrl), Map.class);
            assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
            assertThat(video.get("type")).isEqualTo("Video");
            assertThat(video.get("hashed_id")).isEqualTo(hashedId);
        } finally {
            deleteVideo(hashedId);
        }

        /* make sure callback sent */
        assertThat(callbackData).isEmpty();

        /* Not error */
        File dir = new File(errorDir);
        assertThat(dir).exists();
        assertThat(dir.listFiles()).hasSize(1);

        String content = Joiner.on('\n').join(Files.readAllLines(dir.listFiles()[0].toPath()));
        assertThat(content).contains("name=big_buck_bunny_720p_1mb.mp4");
        assertThat(content).contains("id=" + request.getId());
        assertThat(content).contains("hashed_id=" + hashedId);
    }

    @Test
    public void testUpload_Callback_Timeout() throws Exception {
        callback.stop();

        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl("http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4");

        // @formatter:off
        String hashedId = given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_CREATED)
            .body("name", is("big_buck_bunny_720p_1mb.mp4"))
            .body("type", is("Video"))
            .body("created", notNullValue())
            .body("updated", notNullValue())
        .extract()
            .path("hashed_id")
        ;
        // @formatter:on

        /* make sure video updated */
        String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
        try {
            Map video = http.get(new URI(videoUrl), Map.class);
            assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
            assertThat(video.get("type")).isEqualTo("Video");
            assertThat(video.get("hashed_id")).isEqualTo(hashedId);
        } finally {
            deleteVideo(hashedId);
        }

        /* make sure callback sent */
        assertThat(callbackData).isEmpty();

        /* Not error */
        File dir = new File(errorDir);
        assertThat(dir).exists();
        assertThat(dir.listFiles()).hasSize(1);

        String content = Joiner.on('\n').join(Files.readAllLines(dir.listFiles()[0].toPath()));
        assertThat(content).contains("name=big_buck_bunny_720p_1mb.mp4");
        assertThat(content).contains("id=" + request.getId());
        assertThat(content).contains("hashed_id=" + hashedId);
    }

    //-- Private
    private void deleteVideo(String hashedId) throws Exception {
        try{
            http.delete(new URI("https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword));
        } catch (IOException e){
            LOG.warn("Unable to deleted Video{}", hashedId, e);
        }
    }
}
