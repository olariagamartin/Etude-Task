package com.themarto.etudetask.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Subject extends RealmObject {
    private String title;
    private RealmList<Section> sectionList;

    public Subject(){}

    public Subject(String title) {
        this.title = title;
        sectionList = new RealmList<>();
    }

    public Subject(String title, RealmList<Section> sectionList) {
        this.title = title;
        this.sectionList = sectionList;
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
}
