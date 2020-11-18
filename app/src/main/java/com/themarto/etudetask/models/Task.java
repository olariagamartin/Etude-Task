package com.themarto.etudetask.models;

import io.realm.RealmObject;

public class Task extends RealmObject {
    private String title;
    private String description;
    // TODO: due date

    public Task(){}

    public Task(String title) {
        this.title = title;
        this.description = "";
    }

    public Task(String title, String description, boolean isDone) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateStr(){
        // TODO
        return "Tomorrow, 8:00 AM";
    }
}
