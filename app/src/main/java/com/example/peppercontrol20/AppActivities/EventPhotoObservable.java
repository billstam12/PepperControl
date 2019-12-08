package com.example.peppercontrol20.AppActivities;

import java.util.Observable;

public class EventPhotoObservable extends Observable {
    private static EventPhotoObservable instance = null;

    private EventPhotoObservable() {
    }

    public static EventPhotoObservable getInstance() {
        if (instance == null) {
            instance = new EventPhotoObservable();
        }
        return instance;
    }

    public void sendData(Object data) {
        setChanged();
        notifyObservers(data);
    }
}
