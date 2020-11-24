package com.themarto.etudetask.models;

import java.util.Date;

import io.realm.RealmObject;

public class Task extends RealmObject {
    private String title;
    private String details;
    private Date date;
    private boolean hasAlarm;

    public Task(){}

    public Task(String title) {
        this.title = title;
        this.details = "";
        this.date = null;
        hasAlarm = false;
    }

    public Task(String title, String details) {
        this.title = title;
        this.details = details;
        this.date = null;
        hasAlarm = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateStr(){
        String strDate = "";
        if (date != null){
            if(hasAlarm) {

            } else{

            }
        }
        return strDate;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void removeDate(){
        this.date = null;
    }

    public boolean hasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }
}
