package com.tchepannou.wistia.service.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.wistia.Fixtures;
import com.tchepannou.wistia.exception.VideoAlreadyUploadedException;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Db;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WistiaClientImplTest {
    private String apiPassword = "api-password";

    @Mock
    private Http http;

    @Mock
    private Db db;

    @Mock
    private MetricRegistry metrics;

    @Mock
    private Counter calls;

    @Mock
    private Counter errors;

    @Mock
    Timer timer;

    @Mock
    private Timer.Context duration;

    @InjectMocks
    private WistiaClient wistia = new WistiaClientImpl(apiPassword);


    @Before
    public void setUp (){
        when(metrics.counter(WistiaClientImpl.METRIC_CALLS)).thenReturn(calls);
        when(metrics.counter(WistiaClientImpl.METRIC_ERRORS)).thenReturn(errors);

        when(timer.time()).thenReturn(duration);
        when(metrics.timer(WistiaClientImpl.METRIC_DURATION)).thenReturn(timer);
    }

    @Test
    public void testUpload() throws Exception {
        // Given
        final Video expected = Fixtures.newVideo();
        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(expected);

        // When
        final Video result = wistia.upload("111", "http://glgfkl.com", "12-H@$3d");

        // Then
        assertThat(result).isEqualToComparingFieldByField(expected);

        final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<Class> type = ArgumentCaptor.forClass(Class.class);

        verify(http).post(url.capture(), params.capture(), type.capture());

        assertThat(url.getValue()).isEqualTo("https://upload.wistia.com");
        assertThat(type.getValue()).isEqualTo(Video.class);
        assertThat(params.getValue()).contains(
                MapEntry.entry("project_id", "12-H@$3d"),
                MapEntry.entry("url", "http://glgfkl.com"),
                MapEntry.entry("api_password", apiPassword)
        );
        assertThat(params.getValue()).hasSize(3);

        verify(db).put("111", "http://glgfkl.com");

        verify(calls).inc();
        verify(errors, never()).inc();
        verify(timer).time();
        verify(duration).stop();
    }

    @Test(expected = VideoAlreadyUploadedException.class)
    public void testUpload_AlreadyUploaded() throws Exception {
        // Given
        final Video expected = Fixtures.newVideo();
        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(expected);

        when(db.get("111")).thenReturn("http://glgfkl.com");

        // When
        wistia.upload("111", "http://glgfkl.com", "12-H@$3d");
    }

    @Test
    public void testUpload_Error() throws Exception {
        // Given
        final Video expected = Fixtures.newVideo();
        when(http.post(anyString(), anyMap(), any(Class.class))).thenThrow(IOException.class);

        // When
        try {
            wistia.upload("123", "http://glgfkl.com", "12-H@$3d");
            fail("");
        } catch (IOException e) {
            verify(calls).inc();
            verify(errors).inc();
            verify(timer).time();
            verify(duration).stop();
        }
    }
}
