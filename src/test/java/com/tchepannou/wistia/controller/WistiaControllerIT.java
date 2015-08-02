package com.tchepannou.wistia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.file.Paths;
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
    @Value("${server.port}")
    private int port;

    @Value("${db.directory}")
    private String dbDirectory;

    @Value("${wistia.test_project_hashed_id}")
    private String projectHashedKey;

    @Value("${wistia.api_password}")
    private String apiPassword;

    @Autowired
    private Http http;

    private Map callback;

    private Server server;


    //-- AbstractHandler overrides
    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        callback = new ObjectMapper().readValue(request.getInputStream(), Map.class);
    }

    //-- Tests
    @Before
    public void setUp () throws Exception {
        RestAssured.port = port;

        FileUtils.deleteDirectory(new File(dbDirectory));

        server = new Server(8081);
        server.setHandler(this);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testUpload() throws IOException {
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

        /* local file */
        final File file = Paths.get(dbDirectory, "1", "2", "3", "12345").toFile();
        assertThat(file).exists();
        assertThat(file).hasContent(url);

        /* make sure video uploaded */
        String videoUrl = "https://api.wistia.com/v1/medias/" + hashedId + ".json?api_password=" + apiPassword;
        try {
            Map video = http.get(videoUrl, Map.class);
            assertThat(video.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
            assertThat(video.get("type")).isEqualTo("Video");
            assertThat(video.get("hashed_id")).isEqualTo(hashedId);
        } finally {
            try {
                http.delete(videoUrl);
            } catch (Exception e){

            }
        }

        /* make sure callback sent */
        assertThat(callback).isNotNull();
        assertThat(callback.get("id")).isEqualTo("12345");
        assertThat(callback.get("name")).isEqualTo("big_buck_bunny_720p_1mb.mp4");
        assertThat(callback.get("hashed_id")).isEqualTo(hashedId);
        assertThat(callback.get("event")).isEqualTo("video-uploaded");
        assertThat(callback).containsKey("x-timestamp");
        assertThat(callback).containsKey("x-hash");
    }

    @Test
    public void testUpload_AlreadyUploaded() throws IOException {
        final String url = "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
        final UploadVideoRequest request = new UploadVideoRequest();
        request.setId(String.valueOf("12345"));
        request.setProjectHashId(projectHashedKey);
        request.setUrl(url);

        // @formatter:off
        given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
        .extract()
            .path("hashed_id")
        ;

        given ()
                .contentType(ContentType.JSON)
                .content(request, ObjectMapperType.JACKSON_2)
        .when()
            .put("/api/wistia/video")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_NO_CONTENT)
        ;
        // @formatter:on

        /* local file */
        final File file = Paths.get(dbDirectory, "1", "2", "3", "12345").toFile();
        assertThat(file).exists();
        assertThat(file).hasContent(url);

        /* make sure not callback sent */
        assertThat(callback).isNotNull();
    }
}
