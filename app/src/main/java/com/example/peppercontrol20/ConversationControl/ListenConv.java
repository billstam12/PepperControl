package com.example.peppercontrol20.ConversationControl;


public class ListenConv {
    public int id;
    public int conv_id;
    public String listen;

    public ListenConv() {
        super();
    }

    public ListenConv(int id, int conv_id, String listen) {
        super();
        this.id = id;
        this.conv_id = conv_id;
        this.listen = listen;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getConvId() {
        return conv_id;
    }
    public void setConvId(int conv_id) {
        this.conv_id = conv_id;
    }
    public String getListen() {
        return listen;
    }
    public void setListen(String listen) {
        this.listen = listen;
    }
}
