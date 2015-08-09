package com.tchepannou.wistia.service.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.io.Files;
import com.tchepannou.wistia.Fixtures;
import com.tchepannou.wistia.dto.CallbackResponse;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
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

        File[] files = new File(errorDir).listFiles();
        if (files != null){
            for (File file : files){
                file.delete();
            }
        }
    }

    @Test
    public void testVideoUploaded() throws Exception {
        // Given
        final Video video = Fixtures.newVideo();

        when(http.post(any(URI.class), anyMap(), any(Class.class))).thenReturn(new CallbackResponse("OK"));

        when(hash.generate(anyString(), anyCollection())).thenReturn("this-is-the-hash");

        when(clock.millis()).thenReturn(1234567890L);

        // When
        callback.videoUploaded("123", video);

        final ArgumentCaptor<URI> url = ArgumentCaptor.forClass(URI.class);
        final ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<Class> type = ArgumentCaptor.forClass(Class.class);

        verify(http).postJson(url.capture(), params.capture(), type.capture());
        assertThat(url.getValue()).isEqualTo(new URI(callbackUrl));
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

        when(http.postJson(any(URI.class), anyMap(), any(Class.class))).thenThrow(IOException.class);

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
    }

    @Test
    public void testResend () throws Exception {
        // Given
        File f1 = createErrorFile("1", "v1", "v1");
        File f2 = createErrorFile("2", "v2", "v2");
        File f_1 = createErrorFile("1", "v1_", "v1_");

        // When
        callback.resend();

        // Then
        verify(http, times(2)).postJson(any(URI.class), anyMap(), any(Class.class));

        assertThat(f1).doesNotExist();
        assertThat(f2).doesNotExist();
        assertThat(f_1).doesNotExist();
    }

    private File createErrorFile (String id, String name, String hashedId) throws Exception{
        Properties properties = new Properties();
        properties.put("id", id);
        properties.put("hashed_id", hashedId);
        properties.put("name", name);

        File file = new File(errorDir, System.currentTimeMillis() + "_" + id);
        try(OutputStream out = new FileOutputStream(file)){
            properties.store(out, "");
        }
        return file;
    }
}
