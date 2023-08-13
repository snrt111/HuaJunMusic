package com.snrt.helloworld.music;

import android.net.Uri;

import java.io.Serializable;
import java.util.Objects;

public class MusicVO implements Serializable {

    private String id;
    private Uri uri;

    private String url;
    private  String name;
    private  String duration;
    private  String size;

    private String lyric;

    public MusicVO() {
    }

    public MusicVO(String id, String name, String duration, String size) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    @Override
    public String toString() {
        return "MusicVO{" +
                "id='" + id + '\'' +
                ", uri=" + uri +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", lyric='" + lyric + '\'' +
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
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



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
}
