package com.snrt.helloworld.video;

import android.net.Uri;

import java.io.Serializable;

public class Video implements Serializable {
    private Long id;
    private Uri uri;
    private String name;
    private Long duration;
    private Long size;

    public Video(Long id, Uri uri, String name, Long duration, Long size) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Video{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
