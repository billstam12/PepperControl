package com.example.peppercontrol20.ConversationControl;


import java.util.ArrayList;

public class Conversation {

    int id;
    int event_id;
    ArrayList<ListenConv> listen;
    ArrayList<SayConv> say;
    ArrayList<VideoConv> video;
    ArrayList<PhotoConv> photo;
    String activity;
    String proactive_engagement;
    Integer is_proactive;


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
        this.is_proactive = 0;
        this.proactive_engagement = "";
    }

    public Conversation(int i, int event_id, ArrayList<ListenConv> listen, ArrayList<SayConv> say, ArrayList<VideoConv> video, ArrayList<PhotoConv> photo, String activity, String proactive_engagement) {
        super();
        this.id = i;
        this.event_id = event_id;
        this.listen = listen;
        this.say = say;
        this.video = video;
        this.activity = activity;
        this.photo = photo;
        this.proactive_engagement = proactive_engagement;
        this.is_proactive = 1;
    }

    // constructor
    public Conversation(ArrayList<ListenConv> listen, ArrayList<SayConv> say, ArrayList<VideoConv> video, ArrayList<PhotoConv> photo, String activity) {
        this.listen = listen;
        this.say = say;
        this.activity = activity;
        this.video = video;
        this.photo = photo;
    }

    public String getConversationProactiveEngagement() {
        return proactive_engagement;
    }

    public void setConversationProactiveEngagement(String proactive_engagement) {
        this.proactive_engagement = proactive_engagement;
    }

    public Integer getConversationProactive() {
        return is_proactive;
    }

    public void setConversationProactive(Integer is_proactive) {
        this.is_proactive = is_proactive;
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

    public void setConversationListen(ArrayList<ListenConv> listen) {
        this.listen = listen;
    }

    public ArrayList<SayConv> getConversationSay() {
        return say;
    }

    public void setConversationSay(ArrayList<SayConv> say) {
        this.say = say;
    }

    public String getConversationActivity() {
        return activity;
    }

    public void setConversationActivity(String activity) {
        this.activity = activity;
    }

    public ArrayList<PhotoConv> getConversationPhoto() {
        return photo;
    }

    public void setConversationPhoto(ArrayList<PhotoConv> photo) {
        this.photo = photo;
    }

    public ArrayList<VideoConv> getConversationVideo() {
        return video;
    }

    public void setConversationVideo(ArrayList<VideoConv> video) {
        this.video = video;
    }
}
