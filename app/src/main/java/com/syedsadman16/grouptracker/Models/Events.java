package com.syedsadman16.grouptracker.Models;

import android.widget.ImageView;

public class Events {

    public String hostName, uid, eventid, eventName, eventLocation, eventTime, eventDate, eventDescription, eventImageURL, password;


    public Events(String name, String createdBy, String date, String eventID) {
        eventName = name;
        eventDate = date;
        hostName = createdBy;
        eventid = eventID;
    }

    public String getEventid() { return eventid; }

    public void setEventid(String eventid) { this.eventid = eventid; }

    public String getEventName() {
        return eventName;
    }

    public String getHostName() { return hostName; }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getEventImageURL() { return eventImageURL; }

    public void setEventImageURL(String eventImageURL) { this.eventImageURL = eventImageURL; }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getEventPicture() {
        return eventImageURL;
    }

    public void setEventPicture(String eventImageURL) {
        this.eventImageURL = eventImageURL;
    }
}
