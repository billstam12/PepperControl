package com.example.peppercontrol20.ConversationControl;

import android.net.Uri;

public class PhotoConv {

    public int id;
    public int conversationId;
    public Uri uri;

    public PhotoConv(int id, int conversationId, Uri uri) {
        this.id = id;
        this.uri = uri;
        this.conversationId = conversationId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
