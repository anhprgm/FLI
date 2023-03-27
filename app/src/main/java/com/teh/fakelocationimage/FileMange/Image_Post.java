package com.teh.fakelocationimage.FileMange;

import com.google.gson.annotations.SerializedName;

public class Image_Post {
    @SerializedName("filename")
    private String filename;
    @SerializedName("max_id")
    private String id;

    public Image_Post(String filename, String id) {
        this.filename = filename;
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
