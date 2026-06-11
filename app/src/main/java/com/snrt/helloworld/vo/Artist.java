package com.snrt.helloworld.vo;

/**
 * 歌手信息VO类
 * 包含歌手的基本信息
 *
 * @author HuaJun Music Team
 * @version 1.0
 */
public class Artist {

    private String id;
    private String name;
    private String picUrl;
    private String alias;
    private Integer albumSize;
    private Integer musicSize;

    public Artist() {
    }

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getAlbumSize() {
        return albumSize;
    }

    public void setAlbumSize(Integer albumSize) {
        this.albumSize = albumSize;
    }

    public Integer getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(Integer musicSize) {
        this.musicSize = musicSize;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }
}
