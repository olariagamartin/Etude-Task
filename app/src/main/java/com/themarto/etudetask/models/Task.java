package com.themarto.etudetask.models;

import com.themarto.etudetask.utils.Util;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String note;
    private RealmList<Subtask> subtasks;
    private Date date;
    private boolean done;
    private String alarmStringId;
    private Subject subject;
    private String flagColor;

    public Task(){
        id = UUID.randomUUID().toString();
        this.title = "";
        this.note = "";
        this.subtasks = new RealmList<>();
        this.date = null;
        this.done = false;
        this.alarmStringId = "";
        this.subject = null;
        this.flagColor = Util.FlagColors.NONE;
    }

    public Task(String title, String note, Subject subject) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.note = note;
        this.subtasks = new RealmList<>();
        this.date = null;
        this.done = false;
        this.alarmStringId = "";
        this.subject = subject;
        this.flagColor = Util.FlagColors.NONE;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RealmList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(RealmList<Subtask> subtasks) {
        this.subtasks = subtasks;
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setAlarmStringId(String alarmStringId) {
        this.alarmStringId = alarmStringId;
    }

    public boolean hasAlarm(){
        return !alarmStringId.equals("");
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getFlagColor() {
        return flagColor;
    }

    public void setFlagColor(String flagColor) {
        this.flagColor = flagColor;
    }
}
