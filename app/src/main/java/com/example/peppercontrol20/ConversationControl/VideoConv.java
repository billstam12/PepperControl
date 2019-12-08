package com.example.peppercontrol20.ConversationControl;

public class VideoConv {
    public int id;
    public int conv_id;
    public String url;
    public String name;
    public String description;
    public String category;

    public VideoConv(int id, int conv_id, String url, String category, String description, String name) {
        this.id = id;
        this.conv_id = conv_id;
        this.url = url;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConv_id() {
        return conv_id;
    }

    public void setConv_id(int conv_id) {
        this.conv_id = conv_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
