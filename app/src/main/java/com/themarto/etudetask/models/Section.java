package com.themarto.etudetask.models;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Section extends RealmObject {
    private String title;
    private RealmList<Task> taskList;
    private RealmList<Task> taskDoneList;
    private String id;

    public Section() {}

    public Section(String title) {
        this.title = title;
        this.taskList = new RealmList<>();
        this.taskDoneList = new RealmList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Section(String title, RealmList<Task> taskList) {
        this.title = title;
        this.taskList = taskList;
        this.id = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Task> getTaskList() {
        return taskList;
    }

    public RealmList<Task> getTaskDoneList() {
        return taskDoneList;
    }

    public String getId() {
        return id;
    }
}
