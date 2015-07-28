package com.tchepannou.wistia.service.impl;

import com.tchepannou.wistia.Fixtures;
import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Http;
import com.tchepannou.wistia.service.WistiaClient;
import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WistiaClientImplTest {
    private String apiPassword = "api-password";

    @Mock
    private Http http;

    @InjectMocks
    private WistiaClient wistia = new WistiaClientImpl(apiPassword);

    @Test
    public void testCreateProject() throws Exception {
        // Given
        final Project expected = Fixtures.newProject();
        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(expected);

        // When
        final Project result = wistia.createProject("foo");

        // Then
        assertThat(result).isEqualToComparingFieldByField(expected);

        final ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Map> params = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<Class> type = ArgumentCaptor.forClass(Class.class);

        verify(http).post(url.capture(), params.capture(), type.capture());

        assertThat(url.getValue()).isEqualTo("https://api.wistia.com/v1/projects.json");
        assertThat(type.getValue()).isEqualTo(Project.class);
        assertThat(params.getValue()).contains(
                MapEntry.entry("name", "foo"),
                MapEntry.entry("public", "0"),
                MapEntry.entry("anonymousCanDownload", "0"),
                MapEntry.entry("anonymousCanUpload", "0"),
                MapEntry.entry("api_password", apiPassword)
        );
        assertThat(params.getValue()).hasSize(5);
    }



    @Test
    public void testUpload() throws Exception {
        // Given
        final Video expected = Fixtures.newVideo();
        when(http.post(anyString(), anyMap(), any(Class.class))).thenReturn(expected);

        // When
        final Video result = wistia.upload("http://glgfkl.com", "12-H@$3d");

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
    }
}
