package com.example.peppercontrol20.ConversationControl;

import android.net.Uri;

import java.util.ArrayList;

public class Event {

    int id;
    String name;
    Uri photo;

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    Uri icon;
    ArrayList<Conversation> conversations;

    public Event() {
    }

    public Event(int id, String name, Uri photo, Uri icon) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.icon = icon;
    }

    public Event(int id, String name) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public Uri getIcon() {
        return icon;
    }

    public void setIcon(Uri icon) {
        this.icon = icon;
    }
}
