package com.snrt.helloworld.vo;

import java.util.List;

/**
 * 歌曲信息VO类（增强版）
 * 包含完整的歌曲元数据信息
 *
 * @author HuaJun Music Team
 * @version 2.0
 */
public class Song {

    private String id;
    private String name;

    // 歌手列表（支持多歌手）
    private List<Artist> artists;

    // 专辑信息
    private Album album;

    // 时长（毫秒）
    private Long duration;

    // 音质信息
    private String quality;

    // 歌曲封面URL
    private String picUrl;

    // 播放URL
    private String url;

    // 歌词
    private String lyric;

    // 文件大小
    private Long size;

    // 是否可播放
    private Boolean playable;

    // 歌曲类型（1:单曲, 2:专辑, 3:歌单）
    private Integer type;

    public Song() {
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

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    /**
     * 获取主歌手名称
     *
     * @return 歌手名称，多个歌手用/分隔
     */
    public String getArtistName() {
        if (artists == null || artists.isEmpty()) {
            return "未知歌手";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artists.size(); i++) {
            sb.append(artists.get(i).getName());
            if (i < artists.size() - 1) {
                sb.append(" / ");
            }
        }
        return sb.toString();
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * 获取格式化的时长字符串 (mm:ss)
     *
     * @return 格式化后的时长
     */
    public String getFormattedDuration() {
        if (duration == null || duration <= 0) {
            return "00:00";
        }
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getPlayable() {
        return playable;
    }

    public void setPlayable(Boolean playable) {
        this.playable = playable;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistName='" + getArtistName() + '\'' +
                ", duration=" + duration +
                '}';
    }
}
