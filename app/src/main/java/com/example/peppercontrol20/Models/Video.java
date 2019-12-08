package com.example.peppercontrol20.Models;


public class Video {
    private String name;
    private String desciption;
    private String url;
    private String thumb;
    private String category;

    public Video() {

    }

    public Video(String name, String desciption, String url, String thumb, String category) {
        this.name = name;
        this.url = url;
        this.desciption = desciption;
        this.thumb = thumb;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
