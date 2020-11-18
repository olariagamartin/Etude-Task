package com.themarto.etudetask.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Section extends RealmObject {
    private String title;
    private RealmList<Task> taskList;
    private RealmList<Task> taskDoneList;

    public Section() {}

    public Section(String title) {
        this.title = title;
        this.taskList = new RealmList<>();
        this.taskDoneList = new RealmList<>();
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

    public List<Task> getTaskDoneList() {
        return taskDoneList;
    }
}
