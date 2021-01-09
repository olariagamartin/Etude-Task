package com.themarto.etudetask.models;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    // todo: delete
    private RealmList<Section> sectionList;
    private RealmList<Task> tasks;

    public Subject(){}

    public Subject(String title) {
        this.title = title;
        this.tasks = new RealmList<>();
        sectionList = new RealmList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Subject(String title, RealmList<Section> sectionList) {
        this.title = title;
        this.sectionList = sectionList;
        this.id = UUID.randomUUID().toString();
    }

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(RealmList<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
