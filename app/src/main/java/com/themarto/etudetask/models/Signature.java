package com.themarto.etudetask.models;

import java.util.List;

public class Signature {
    private String title;
    private List<Chapter> chapterList;

    public Signature() {
    }

    public Signature(String title, List<Chapter> chapterList) {
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

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }
}
