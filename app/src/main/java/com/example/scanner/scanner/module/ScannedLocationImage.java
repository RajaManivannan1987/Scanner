package com.example.scanner.scanner.module;

/**
 * Created by IM021 on 11/23/2015.
 */
public class ScannedLocationImage {
    private Long key;
    private String path;
    private String originalImagePath;
    private String ocrPath;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalImagePath() {
        return originalImagePath;
    }

    public void setOriginalImagePath(String originalImagePath) {
        this.originalImagePath = originalImagePath;
    }

    public String getOcrPath() {
        return ocrPath;
    }

    public void setOcrPath(String ocrPath) {
        this.ocrPath = ocrPath;
    }
}
