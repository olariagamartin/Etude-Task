package com.themarto.etudetask;

import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SubjectRepository {

    private Realm realm;

    public SubjectRepository() {
        realm = Realm.getDefaultInstance();
    }

    public Subject getSubject (String id) {
        Subject subject = realm.where(Subject.class).equalTo("id", id).findFirst();
        return realm.copyFromRealm(subject);
    }

    public void closeRealm() {
        realm.close();
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjectList = realm.where(Subject.class).findAll();
        return realm.copyFromRealm(subjectList);
    }

    public List<Task> getTodayTaskList () {
        Calendar startOfDay = Calendar.getInstance();
        Calendar endOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        endOfDay.setTime(startOfDay.getTime());
        endOfDay.set(Calendar.DAY_OF_MONTH, startOfDay.get(Calendar.DAY_OF_MONTH) + 1);
        List<Task> todayList = realm.where(Task.class).between("date", startOfDay.getTime(), endOfDay.getTime())
                .and().equalTo("done", false).findAll();
        return realm.copyFromRealm(todayList);
    }

    public List<Task> getUpcomingTaskList () {
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, 0);
        endOfDay.set(Calendar.MINUTE, 0);
        endOfDay.set(Calendar.SECOND, 0);
        endOfDay.set(Calendar.DAY_OF_MONTH, endOfDay.get(Calendar.DAY_OF_MONTH) + 1);
        List<Task> upcomingList = realm.where(Task.class).greaterThan("date", endOfDay.getTime())
                .and().equalTo("done", false).sort("date").findAll();
        return realm.copyFromRealm(upcomingList);
    }

    public void updateSubjectList(List<Subject> subjectList) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(subjectList);
        realm.commitTransaction();
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
        for(Task t : subject.getTaskList()) {
            deleteTask(t);
        }
        realm.beginTransaction();
        Subject managedSubject = realm.where(Subject.class).equalTo("id", subject.getId()).findFirst();
        managedSubject.deleteFromRealm();
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

    public Subject addTask(Subject subject, Task nTask) {
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
        Task manageTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
        manageTask.setAlarmStringId("");
        Task deletedTask = realm.copyFromRealm(manageTask);
        manageTask.getSubtasks().deleteAllFromRealm();
        manageTask.deleteFromRealm();
        realm.commitTransaction();
        return deletedTask;
    }

    public Task getTask(String taskId) {
        Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
        return realm.copyFromRealm(task);
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

    public void deleteSubtasks (Task task) {
        realm.beginTransaction();
        Task managedTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
        managedTask.getSubtasks().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public Task setTaskDone(Task task) {
        realm.beginTransaction();
        task.setDone(true);
        task.setAlarmStringId("");
        realm.commitTransaction();
        return task;
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
