package com.snrt.helloworld.music;

import android.net.Uri;

import java.io.Serializable;
import java.util.Objects;

public class MusicVO implements Serializable {

    private Long id;
    private Uri uri;
    private  String name;
    private  Long duration;
    private  Long size;

    public MusicVO(Long id, String name, Long duration, Long size) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    @Override
    public String toString() {
        return "MusicVO{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicVO musicVO = (MusicVO) o;
        return duration == musicVO.duration && size == musicVO.size && Objects.equals(name, musicVO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration, size);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
