package com.example.peppercontrol20.ConversationControl;


import android.net.Uri;

import java.util.ArrayList;

public class Conversation{

    int id;
    int event_id;
    ArrayList<ListenConv> listen;
    ArrayList<SayConv> say;
    ArrayList<VideoConv> video;
    ArrayList<PhotoConv> photo;
    String activity;


    public Conversation() {
        super();
    }
    public Conversation(int i, int event_id, ArrayList<ListenConv> listen, ArrayList<SayConv> say, ArrayList<VideoConv> video, ArrayList<PhotoConv> photo, String activity) {
        super();
        this.id = i;
        this.event_id = event_id;
        this.listen = listen;
        this.say = say;
        this.video = video;
        this.activity = activity;
        this.photo = photo;
    }

    // constructor
    public Conversation(ArrayList<ListenConv> listen, ArrayList<SayConv> say, ArrayList<VideoConv> video,  ArrayList<PhotoConv> photo,  String activity){
        this.listen = listen;
        this.say = say;
        this.activity = activity;
        this.video = video;
        this.photo = photo;
    }

    public int getConversationEventID() {
        return event_id;
    }

    public void setConversationEventID(int event_id) {
        this.event_id = event_id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public ArrayList<ListenConv> getConversationListen() {
        return listen;
    }
    public ArrayList<SayConv> getConversationSay() {
        return say;
    }
    public String getConversationActivity() { return activity; }
    public ArrayList<PhotoConv> getConversationPhoto() { return  photo; }
    public void setConversationListen(ArrayList<ListenConv> listen) {
        this.listen = listen;
    }
    public void setConversationSay(ArrayList<SayConv> say) {
        this.say = say;
    }
    public void setConversationActivity(String activity) {this.activity = activity;}
    public void setConversationPhoto(ArrayList<PhotoConv> photo) {this.photo = photo;}
    public ArrayList<VideoConv> getConversationVideo() {
        return video;
    }

    public void setConversationVideo(ArrayList<VideoConv> video) {
        this.video = video;
    }
}
