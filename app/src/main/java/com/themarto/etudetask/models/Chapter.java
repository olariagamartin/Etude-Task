package com.themarto.etudetask.models;

import java.util.List;

public class Chapter {
    private String title;
    private List<Task> taskList;

    public Chapter() {
    }

    public Chapter(String title, List<Task> taskList) {
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

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
