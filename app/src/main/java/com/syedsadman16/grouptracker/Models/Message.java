package com.syedsadman16.grouptracker.Models;

public class Message {

    String userid, time, date, message, senderName;

    public Message(String UID, String message, String sender, String time ){
        this.userid = UID;
        this.message = message;
        this.senderName = sender;
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }
}
