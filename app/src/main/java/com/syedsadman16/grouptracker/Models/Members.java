package com.syedsadman16.grouptracker.Models;

public class Members {

    String name, fullName, userid, latitude, longitude;

    public Members(String mName, String mLat, String mLong){
        this.name = mName;
        this.latitude = mLat;
        this.longitude = mLong;
        // this,image = mImage;
    }

    public Members(String mName, String mUserid){
        this.fullName = mName;
        this.userid = mUserid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String full_name) {
        this.fullName = full_name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
