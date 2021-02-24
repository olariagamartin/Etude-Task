package com.themarto.etudetask.data;

import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SubjectRepository {

    private Realm realm;

    private RealmResults<Subject> subjects;

    public SubjectRepository() {
        realm = Realm.getDefaultInstance();
        subjects = realm.where(Subject.class).sort("title").findAll();
    }

    public void closeRealm() {
        realm.close();
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
        int tasks = subject.getTaskList().size();
        for(int i = 0; i < tasks; i++) {
            deleteTask(subject.getTaskList().first());
        }
        realm.beginTransaction();
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
        realm.close();
    }

    public Subject  addTask(Subject subject, Task nTask) {
        realm.beginTransaction();
        nTask.setSubject(subject);
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
        Task manageTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
        manageTask.setAlarmStringId("");
        Task deletedTask = realm.copyFromRealm(manageTask);
        manageTask.getSubtasks().deleteAllFromRealm();
        manageTask.deleteFromRealm();
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

    public void setTaskDone(Task task) {
        realm.beginTransaction();
        task.setDone(true);
        task.setAlarmStringId("");
        realm.commitTransaction();
    }

    public static void setTaskDone(String taskId) {
        Realm realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
        if(task != null){
            realm.beginTransaction();
            task.setAlarmStringId("");
            task.setDone(true);
            realm.commitTransaction();
        }
        realm.close();
    }

    public void setTaskUndone(Task task) {
        realm.beginTransaction();
        task.setDone(false);
        realm.commitTransaction();
    }
}
