package com.themarto.etudetask.models;

import io.realm.RealmObject;

public class Subtask extends RealmObject {
    private String title;
    private boolean isDone;

    public Subtask() {
        this.title = "";
        this.isDone = false;
    }

    public Subtask(String title) {
        this.title = title;
        this.isDone = false;
    }

    public Subtask(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
