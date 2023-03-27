package com.teh.fakelocationimage.models;

import java.io.Serializable;

public class EditImageCase implements Serializable {
    private String uri = "";
    private String name = "";

    public EditImageCase(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
