package com.huajun.music.model;

import java.io.Serializable;

public class Album implements Serializable {

    private String id;
    private String name;
    private String picUrl;

    public Album() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }
}
