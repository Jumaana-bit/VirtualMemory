package com.example.virtualmemory;

public class MediaItem {
    private String path;
    private String label;
    private boolean isVideo;

    public MediaItem(String path, String label, boolean isVideo) {
        this.path = path;
        this.label = label;
        this.isVideo = isVideo;
    }

    public String getPath() { return path; }
    public String getLabel() { return label; }
    public boolean isVideo() { return isVideo; }
}

