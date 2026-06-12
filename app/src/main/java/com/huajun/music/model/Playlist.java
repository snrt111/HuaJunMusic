package com.huajun.music.model;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {

    private String id;
    private String name;
    private String description;
    private String coverImgUrl;
    private int trackCount;
    private long playCount;
    private List<Song> tracks;

    public Playlist() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImgUrl() { return coverImgUrl; }
    public void setCoverImgUrl(String coverImgUrl) { this.coverImgUrl = coverImgUrl; }

    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) { this.trackCount = trackCount; }

    public long getPlayCount() { return playCount; }
    public void setPlayCount(long playCount) { this.playCount = playCount; }

    public List<Song> getTracks() { return tracks; }
    public void setTracks(List<Song> tracks) { this.tracks = tracks; }
}
