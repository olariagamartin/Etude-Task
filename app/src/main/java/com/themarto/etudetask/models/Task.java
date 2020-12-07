package com.themarto.etudetask.models;

import com.themarto.etudetask.utils.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String details;
    private Date date;
    private String alarmStringId;

    public Task(){}

    public Task(String title) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.details = "";
        this.date = null;
        this.alarmStringId = "";
    }

    public Task(String title, String details) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.details = details;
        this.date = null;
        this.alarmStringId = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
            if(hasAlarm()) {
                strDate = Util.getDateString(date) + ", " + Util.getTimeString(date);
            } else{
                strDate = Util.getDateString(date);
            }
        }
        return strDate;
    }

    public Date getDate() { return this.date; }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAlarmStringId() {
        return alarmStringId;
    }

    public void setAlarmStringId(String alarmStringId) {
        this.alarmStringId = alarmStringId;
    }

    public boolean hasAlarm(){
        return !alarmStringId.equals("");
    }
}
