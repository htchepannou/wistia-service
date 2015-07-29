package com.tchepannou.wistia;

import com.tchepannou.wistia.dto.CreateProjectRequest;
import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.model.Project;
import com.tchepannou.wistia.model.Thumbnail;
import com.tchepannou.wistia.model.Video;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

public class Fixtures {
    public static Video newVideo(){
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setHeight(100);
        thumbnail.setWidth(200);
        thumbnail.setUrl("http://www.goog.ca");

        Video video = new Video();
        video.setId(12);
        video.setCreated(new Date());
        video.setUpdated(new Date());
        video.setType("mpeg");
        video.setHashedId("12-haSh3d");
        video.setDuration(200);
        video.setName("foo");
        video.setThumbnail(thumbnail);
        return video;
    }

    public static Project newProject(){
        Project obj = new Project();
        obj.setId(12);
        obj.setName("foo");
        obj.setAnonymousCanDownload(true);
        obj.setAnonymousCanUpload(true);
        obj.setHashedId("12-hashed");
        obj.setPublicId("111");
        obj.setMediaCount(12);
        obj.setCreated(new Date());
        obj.setUpdated(DateUtils.addDays(new Date(), -2));
        return obj;
    }

    public static CreateProjectRequest  newCreateProjectRequest(){
        CreateProjectRequest obj = new CreateProjectRequest();
        obj.setName("foo");
        obj.setId("11");
        return obj;
    }

    public static UploadVideoRequest newUploadVideoRequest (){
        UploadVideoRequest obj = new UploadVideoRequest();
        obj.setId("32");
        obj.setUrl("http://fdlkd.com");
        obj.setProjectHashId("1221");
        return obj;
    }
}
