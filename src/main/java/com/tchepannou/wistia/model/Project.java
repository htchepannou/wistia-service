package com.tchepannou.wistia.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class Project {
    //-- Attributes
    private long id;
    private String name;
    private long mediaCount;

    @XmlElement(name = "hashed_id")
    private String hashedId;
    private boolean anonymousCanUpload;
    private boolean anonymousCanDownload;
    private String publicId;
    private Date created;
    private Date updated;


    //-- Getter/Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(long mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getHashedId() {
        return hashedId;
    }

    public void setHashedId(String hashedId) {
        this.hashedId = hashedId;
    }

    public boolean isAnonymousCanUpload() {
        return anonymousCanUpload;
    }

    public void setAnonymousCanUpload(boolean anonymousCanUpload) {
        this.anonymousCanUpload = anonymousCanUpload;
    }

    public boolean isAnonymousCanDownload() {
        return anonymousCanDownload;
    }

    public void setAnonymousCanDownload(boolean anonymousCanDownload) {
        this.anonymousCanDownload = anonymousCanDownload;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
