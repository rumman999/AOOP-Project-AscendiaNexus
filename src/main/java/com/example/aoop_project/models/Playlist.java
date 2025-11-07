package com.example.aoop_project.models;

public class Playlist {
    private final String name;
    private final String url;
    public Playlist(String name, String url) {
        this.name = name; this.url = url;
    }
    public String getName() { return name; }
    public String getUrl()  { return url; }
    @Override public String toString() { return name; }
}
