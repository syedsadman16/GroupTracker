package com.syedsadman16.grouptracker.Models;

import android.widget.ImageView;

public class Events {

    public String createdBy, uid, eventid, eventName, eventLocation, eventTime, eventDate, eventDescription, eventImageURL, eventPassword;

    // Constructor for Adapter
    public Events(String name, String ownerName, String date, String eventID, String image) {
        eventName = name;
        eventDate = date;
        createdBy = ownerName;
        eventid = eventID;
        eventImageURL = image;
    }

    // Constructor for EventCreation
    public Events(String EventID, String ownerName, String Date, String Description, String Image,
                  String Location, String Name, String Password, String Time, String Uid) {
        eventid = EventID;
        createdBy = ownerName;
        eventDate = Date;
        eventDescription = Description;
        eventImageURL = Image;
        eventLocation = Location;
        eventName = Name;
        eventPassword = Password;
        eventTime = Time;
        uid = Uid;
    }

    public String getEventid() { return eventid; }
    public void setEventid(String eventid) { this.eventid = eventid; }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public String getEventName() {
        return eventName;
    }

    public void setHostName(String hostName) { this.createdBy = hostName; }
    public String getHostName() { return createdBy; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getPassword() { return eventPassword; }
    public void setPassword(String password) { this.eventPassword = password; }


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
