package com.themarto.etudetask.data;

import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Subtask;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SubjectRepository {

    private Realm realm;

    private RealmResults<Subject> subjects;

    public SubjectRepository() {
        realm = Realm.getDefaultInstance();
        subjects = realm.where(Subject.class).sort("title").findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        realm.beginTransaction();
        realm.copyToRealm(subject);
        realm.commitTransaction();
    }

    public Subject updateSubject(Subject subject){
        realm.beginTransaction();
        Subject updateSubject = realm.copyToRealmOrUpdate(subject);
        realm.commitTransaction();
        return updateSubject;
    }

    public void deleteSubject(Subject subject) {
        realm.beginTransaction();
        subject.getTaskList().deleteAllFromRealm();
        subject.deleteFromRealm();
        realm.commitTransaction();
    }

    public static void setAlarmTaskDone(String taskId){
        Realm realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
        if (task != null){
            realm.beginTransaction();
            task.setAlarmStringId("");
            realm.commitTransaction();
        }
    }

    public Subject  addTask(Subject subject, Task nTask) {
        realm.beginTransaction();
        subject.getTaskList().add(nTask);
        realm.commitTransaction();
        return subject;
    }

    public Subject addTask(Subject subject, Task nTask, int position){
        realm.beginTransaction();
        subject.getTaskList().add(position, nTask);
        realm.commitTransaction();
        return subject;
    }

    public void updateTask(Task task) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(task);
        realm.commitTransaction();
    }

    public Task deleteTask(Task task) {
        realm.beginTransaction();
        // todo: verify if deletedTask contains subtasks, remove notification
        Task nTask = realm.copyToRealmOrUpdate(task);
        Task deletedTask = realm.copyFromRealm(nTask);
        nTask.getSubtasks().deleteAllFromRealm();
        nTask.deleteFromRealm();
        realm.commitTransaction();
        return deletedTask;
    }

    public Subject deleteAllCompletedTasks(Subject subject) {
        List<Task> doneTasks = new ArrayList<>();
        for (Task task : subject.getTaskList()) {
            if (task.isDone()) doneTasks.add(task);
                // I have problems when delete the task here
        }
        for(Task task : doneTasks)
            deleteTask(task);
        return subject;
    }

    public Section deleteTask(Section section, int position) {
        realm.beginTransaction();
        section.getTaskList().get(position).deleteFromRealm();
        realm.commitTransaction();
        return section;
    }

    public void setTaskDone(Task task) {
        realm.beginTransaction();
        task.setDone(true);
        task.setAlarmStringId("");
        realm.commitTransaction();
    }

    public void setTaskDone(String taskId, String sectionId) {
        Section section = realm.where(Section.class).equalTo("id", sectionId).findFirst();
        Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
        if(section != null && task != null){
            realm.beginTransaction();
            section.getTaskList().remove(task);
            task.setAlarmStringId("");
            section.getTaskDoneList().add(task);
            realm.commitTransaction();
        }
    }

    public void setTaskUndone(Task task) {
        realm.beginTransaction();
        task.setDone(false);
        realm.commitTransaction();
    }

    public Task addSubtask(Task task, Subtask subtask) {
        realm.beginTransaction();
        task.getSubtasks().add(subtask);
        realm.commitTransaction();
        return  task;
    }
}