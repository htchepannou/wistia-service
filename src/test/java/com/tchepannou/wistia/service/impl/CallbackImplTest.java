package com.tchepannou.wistia.service.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.io.Files;
import com.tchepannou.wistia.Fixtures;
import com.tchepannou.wistia.dto.CallbackResponse;
import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.HashGenerator;
import com.tchepannou.wistia.service.Http;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Clock;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallbackImplTest {
    private String callbackUrl = "http://goo.ca/callback";

    private String apiKey = "4309534kfdlfkdlkvmc,mgf";

    private String errorDir = Files.createTempDir().getAbsolutePath();

    @Mock
    private Clock clock;

    @Mock
    private Http http;

    @Mock
    private MetricRegistry metrics;

    @Mock
    private Counter calls;

    @Mock
    private Counter errors;

    @Mock
    private Counter spool;

    @Mock
    Timer timer;

    @Mock
    private Timer.Context duration;


    @Mock
    private HashGenerator hash;

    @InjectMocks
    private Callback callback = new CallbackImpl(callbackUrl, errorDir, apiKey);


    @Before
    public void setUp (){
        when(metrics.counter(CallbackImpl.METRIC_CALLS)).thenReturn(calls);
        when(metrics.counter(CallbackImpl.METRIC_ERRORS)).thenReturn(errors);
        when(metrics.counter(CallbackImpl.METRIC_SPOOL_SIZE)).thenReturn(spool);

        when(timer.time()).thenReturn(duration);
        when(metrics.timer(CallbackImpl.METRIC_DURATION)).thenReturn(timer);
    }


    @Test
    public void testProjectCreated() throws Exception {
        // Given
        final Project project = Fixtures.newProject();

        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(new CallbackResponse("IGNORED"));

        when(hash.generate(anyString(), anyCollection())).thenReturn("this-is-the-hash");

        when(clock.millis()).thenReturn(1234567890L);

        // When
        callback.projectCreated("123", project);

        final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<Class> type = ArgumentCaptor.forClass(Class.class);

        verify(http).postJson(url.capture(), params.capture(), type.capture());
        assertThat(url.getValue()).isEqualTo(callbackUrl);
        assertThat(type.getValue()).isEqualTo(CallbackResponse.class);
        assertThat(params.getValue()).containsExactly(
                MapEntry.entry("event", "project-created"),
                MapEntry.entry("id", "123"),
                MapEntry.entry("name", project.getName()),
                MapEntry.entry("hashed_id", project.getHashedId()),
                MapEntry.entry("x-timestamp", "1234567890"),
                MapEntry.entry("x-hash", "this-is-the-hash")
        );

        verify(calls).inc();
        verify(timer).time();
        verify(duration).stop();
        verify(errors, never()).inc();
        verify(spool, never()).inc();
    }

    @Test
    public void testProjectCreated_httpError() throws Exception {
        // Given
        final Project project = Fixtures.newProject();

        when(http.postJson(anyString(), anyMap(), any(Class.class))).thenThrow(IOException.class);

        when(hash.generate(anyString(), anyCollection())).thenReturn("this-is-the-hash");

        long now = System.currentTimeMillis();
        when(clock.millis()).thenReturn(now);

        // When
        callback.projectCreated("123", project);

        // Then
        File file = new File(errorDir, now + "-" + "project-123");
        assertThat(file).exists();

        List<String> content = Files.readLines(file, Charset.defaultCharset());
        assertThat(content).contains(
                "x-hash=this-is-the-hash",
                "event=project-created",
                "name=" + project.getName(),
                "hashed_id=" + project.getHashedId(),
                "id=123",
                "x-timestamp=" + now,
                "x-hash=this-is-the-hash"
        );

        verify(calls).inc();
        verify(timer).time();
        verify(duration).stop();
        verify(errors).inc();
        verify(spool).inc();
    }

    @Test
    public void testVideoUploaded() throws Exception {
        // Given
        final Video video = Fixtures.newVideo();

        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(new CallbackResponse("OK"));

        when(hash.generate(anyString(), anyCollection())).thenReturn("this-is-the-hash");

        when(clock.millis()).thenReturn(1234567890L);

        // When
        callback.videoUploaded("123", video);

        final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<Class> type = ArgumentCaptor.forClass(Class.class);

        verify(http).postJson(url.capture(), params.capture(), type.capture());
        assertThat(url.getValue()).isEqualTo(callbackUrl);
        assertThat(type.getValue()).isEqualTo(CallbackResponse.class);
        assertThat(params.getValue()).containsExactly(
                MapEntry.entry("event", "video-uploaded"),
                MapEntry.entry("id", "123"),
                MapEntry.entry("name", video.getName()),
                MapEntry.entry("hashed_id", video.getHashedId()),
                MapEntry.entry("x-timestamp", "1234567890"),
                MapEntry.entry("x-hash", "this-is-the-hash")
        );

        verify(calls).inc();
        verify(timer).time();
        verify(duration).stop();
        verify(errors, never()).inc();
        verify(spool, never()).inc();
    }

    @Test
    public void testVideoUploaded_httpError() throws Exception {
        // Given
        final Video video = Fixtures.newVideo();

        when(http.postJson(anyString(), anyMap(), any(Class.class))).thenThrow(IOException.class);

        when(hash.generate(anyString(), anyCollection())).thenReturn("this-is-the-hash");

        long now = System.currentTimeMillis();
        when(clock.millis()).thenReturn(now);

        // When
        callback.videoUploaded("123", video);

        // Then
        File file = new File(errorDir, now + "-" + "video-123");
        assertThat(file).exists();

        List<String> content = Files.readLines(file, Charset.defaultCharset());
        assertThat(content).contains(
                "x-hash=this-is-the-hash",
                "event=video-uploaded",
                "name=" + video.getName(),
                "hashed_id=" + video.getHashedId(),
                "id=123",
                "x-timestamp=" + now,
                "x-hash=this-is-the-hash"
        );

        verify(calls).inc();
        verify(timer).time();
        verify(duration).stop();
        verify(errors).inc();
        verify(spool).inc();
    }}
