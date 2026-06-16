package com.huajun.music.model;

import java.io.Serializable;
import java.util.List;

public class Song implements Serializable {

    private String id;
    private String name;
    private List<Artist> artists;
    private Album album;
    private Long duration;
    private String url;
    private String picUrl;
    private String lyric;
    private Long size;
    private String quality;
    
    // 酷狗音乐API专用字段
    private String hash;      // 歌曲hash值，用于获取播放URL
    private String albumId;   // 专辑ID，用于获取播放URL

    public Song() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Artist> getArtists() { return artists; }
    public void setArtists(List<Artist> artists) { this.artists = artists; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPicUrl() {
        if (picUrl != null && !picUrl.isEmpty()) return picUrl;
        if (album != null && album.getPicUrl() != null) return album.getPicUrl();
        return null;
    }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public String getLyric() { return lyric; }
    public void setLyric(String lyric) { this.lyric = lyric; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    // 酷狗音乐API专用方法
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public String getAlbumId() { return albumId; }
    public void setAlbumId(String albumId) { this.albumId = albumId; }

    public String getArtistName() {
        if (artists == null || artists.isEmpty()) return "未知歌手";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artists.size(); i++) {
            sb.append(artists.get(i).getName());
            if (i < artists.size() - 1) sb.append(" / ");
        }
        return sb.toString();
    }

    public String getAlbumName() {
        return album != null ? album.getName() : "未知专辑";
    }

    public String getFormattedDuration() {
        if (duration == null || duration <= 0) return "00:00";
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public String getDisplayDuration() {
        return getFormattedDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id != null ? id.equals(song.id) : song.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
