package com.themarto.etudetask.models;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private RealmList<Task> taskList;

    public Subject(){
        this.title = "";
        this.taskList = new RealmList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Subject(String title) {
        this.title = title;
        this.taskList = new RealmList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Subject(String title, RealmList<Task> taskList) {
        this.title = title;
        this.taskList = taskList;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(RealmList<Task> taskList) {
        this.taskList = taskList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDoneSize(){
        int doneCount = 0;
        for(Task task : taskList){
            if(task.isDone()) doneCount++;
        }
        return doneCount;
    }

}
