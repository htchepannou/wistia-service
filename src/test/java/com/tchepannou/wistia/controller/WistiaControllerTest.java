package com.tchepannou.wistia.controller;

import com.tchepannou.wistia.Fixtures;
import com.tchepannou.wistia.dto.CreateProjectRequest;
import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Video;
import com.tchepannou.wistia.service.Callback;
import com.tchepannou.wistia.service.WistiaClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WistiaControllerTest {
    @Mock
    private WistiaClient client;

    @Mock
    private Callback callback;

    @InjectMocks
    private WistiaController controller = new WistiaController();

    @Test
    public void testCreateProject() throws Exception {
        // Given
        CreateProjectRequest request = Fixtures.newCreateProjectRequest();

        Project project = Fixtures.newProject();
        when(client.createProject(request.getName())).thenReturn(project);

        // When
        controller.createProject(request);

        // Then
        verify(callback).projectCreated(request.getId(), project);
    }

    @Test(expected = IOException.class)
    public void testCreateProject_IOException() throws Exception {
        // Given
        CreateProjectRequest request = Fixtures.newCreateProjectRequest();

        when(client.createProject(request.getName())).thenThrow(IOException.class);

        // When
        controller.createProject(request);
    }

    @Test
    public void testUploadVideo() throws Exception {
        // Given
        UploadVideoRequest request = Fixtures.newUploadVideoRequest();

        Video video = Fixtures.newVideo();
        when(client.upload(request.getUrl(), request.getProjectHashId())).thenReturn(video);

        // When
        controller.uploadVideo(request);

        // Then
        verify(callback).videoUploaded(request.getId(), video);
    }
}
