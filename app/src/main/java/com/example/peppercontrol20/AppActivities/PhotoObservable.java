package com.example.peppercontrol20.AppActivities;

import java.util.Observable;

public class PhotoObservable extends Observable {
    private static PhotoObservable instance = null;

    private PhotoObservable() {
    }

    public static PhotoObservable getInstance() {
        if (instance == null) {
            instance = new PhotoObservable();
        }
        return instance;
    }

    public void sendData(Object data) {
        setChanged();
        notifyObservers(data);
    }
}
