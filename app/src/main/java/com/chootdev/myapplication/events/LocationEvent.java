package com.chootdev.myapplication.events;

import com.orm.SugarRecord;

/**
 * Created by Choota on 2/7/18.
 */

public class LocationEvent extends SugarRecord {
    private String timeStamp;
    private String lat;
    private String lan;
    private String distence;

    public LocationEvent() {
    }

    public LocationEvent(String timeStamp, String lat, String lan, String distence) {
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lan = lan;
        this.distence = distence;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getDistence() {
        return distence;
    }

    public void setDistence(String distence) {
        this.distence = distence;
    }
}
