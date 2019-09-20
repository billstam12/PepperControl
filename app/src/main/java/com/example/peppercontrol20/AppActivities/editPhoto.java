package com.example.peppercontrol20.AppActivities;

import android.net.Uri;

public class editPhoto {
    Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public editPhoto(Integer id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    String uri;
}
