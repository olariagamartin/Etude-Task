package com.themarto.etudetask.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Signature extends RealmObject {
    private String title;
    private RealmList<Chapter> chapterList;

    public Signature(){}

    public Signature(String title) {
        this.title = title;
        chapterList = new RealmList<>();
    }

    public Signature(String title, RealmList<Chapter> chapterList) {
        this.title = title;
        this.chapterList = chapterList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(RealmList<Chapter> chapterList) {
        this.chapterList = chapterList;
    }
}
