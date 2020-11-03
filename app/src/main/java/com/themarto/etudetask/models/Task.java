package com.themarto.etudetask.models;

public class Task {
    private String title;
    private String description;
    private boolean isDone;
    // TODO: due date


    public Task() {
    }

    public Task(String title, String description, boolean isDone) {
        this.title = title;
        this.description = description;
        this.isDone = isDone;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getDateStr(){
        // TODO
        return "Tomorrow, 8:00 AM";
    }
}
