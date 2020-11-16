package com.themarto.etudetask.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Section extends RealmObject {
    private String title;
    private RealmList<Task> taskList;

    public Section() {}

    public Section(String title) {
        this.title = title;
        this.taskList = new RealmList<>();
    }

    public Section(String title, RealmList<Task> taskList) {
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
