package com.themarto.etudetask.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subtask extends RealmObject {

    @PrimaryKey
    private String id;
    private String title;
    private boolean isDone;

    public Subtask() {
        id = UUID.randomUUID().toString();
        this.title = "";
        this.isDone = false;
    }

    public Subtask(String title) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.isDone = false;
    }

    public Subtask(String title, boolean isDone) {
        id = UUID.randomUUID().toString();
        this.title = title;
        this.isDone = isDone;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
