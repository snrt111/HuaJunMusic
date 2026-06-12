package com.huajun.music.model;

import java.io.Serializable;

public class Artist implements Serializable {

    private String id;
    private String name;
    private String picUrl;
    private String alias;

    public Artist() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}
