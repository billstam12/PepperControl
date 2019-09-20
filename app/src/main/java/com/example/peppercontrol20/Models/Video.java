package com.example.peppercontrol20.Models;


public class Video {
    private  String name;
    private String desciption;
    private String url;
    private String thumb;
    private String category;

    public Video(){

    }

    public Video(String name, String desciption, String url, String thumb, String category){
        this.name = name;
        this.url = url;
        this.desciption = desciption;
        this.thumb = thumb;
        this.category = category;
    }

    public String getName(){
        return name;
    }

    public  String getDesciption(){
        return desciption;
    }

    public String getUrl(){
        return url;
    }

    public String getThumb(){
        return thumb;
    }

    public String getCategory(){
        return category;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCategory(String category){
        this.category = category;
    }
}
