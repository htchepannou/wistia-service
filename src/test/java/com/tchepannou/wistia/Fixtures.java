package com.tchepannou.wistia;

import com.tchepannou.wistia.dto.UploadVideoRequest;
import com.tchepannou.wistia.model.Thumbnail;
import com.tchepannou.wistia.model.Video;

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


    public static UploadVideoRequest newUploadVideoRequest (){
        UploadVideoRequest obj = new UploadVideoRequest();
        obj.setId(String.valueOf(System.currentTimeMillis()));
        obj.setUrl("http://fdlkd.com");
        obj.setProjectHashId("1221");
        return obj;
    }
}
