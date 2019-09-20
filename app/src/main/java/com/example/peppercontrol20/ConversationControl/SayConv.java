package com.example.peppercontrol20.ConversationControl;


public class SayConv {
    public int id;
    public int conv_id;
    public String say;

    public SayConv() {
        super();
    }

    public SayConv(int id, int conv_id, String say) {
        super();
        this.id = id;
        this.conv_id = conv_id;
        this.say = say;
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
    public String getSay() {
        return say;
    }
    public void setSay(String say) {
        this.say = say;
    }
}
