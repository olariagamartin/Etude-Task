package com.themarto.etudetask.models;

import io.realm.RealmObject;

public class Task extends RealmObject {
    private String title;
    private String details;
    // TODO: due date

    public Task(){}

    public Task(String title) {
        this.title = title;
        this.details = "";
    }

    public Task(String title, String details) {
        this.title = title;
        this.details = details;
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
        // TODO
        return "Tomorrow, 8:00 AM";
    }
}
