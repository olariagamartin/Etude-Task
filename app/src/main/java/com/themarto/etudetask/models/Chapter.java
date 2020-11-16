package com.themarto.etudetask.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Chapter extends RealmObject {
    private String title;
    private RealmList<Task> taskList;

    public Chapter() {}

    public Chapter(String title) {
        this.title = title;
        this.taskList = new RealmList<>();
    }

    public Chapter(String title, RealmList<Task> taskList) {
        this.title = title;
        this.taskList = taskList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(RealmList<Task> taskList) {
        this.taskList = taskList;
    }
}
