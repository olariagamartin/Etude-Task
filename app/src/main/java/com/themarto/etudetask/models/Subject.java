package com.themarto.etudetask.models;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Subject extends RealmObject {
    private String title;
    private RealmList<Section> sectionList;
    private String id;

    public Subject(){}

    public Subject(String title) {
        this.title = title;
        sectionList = new RealmList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Subject(String title, RealmList<Section> sectionList) {
        this.title = title;
        this.sectionList = sectionList;
        this.id = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(RealmList<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public String getId() {
        return id;
    }
}
