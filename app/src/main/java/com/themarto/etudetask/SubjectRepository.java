package com.themarto.etudetask;

import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.realm.Realm;
import io.realm.RealmResults;

public class SubjectRepository {

    private Realm realm;

    private RealmResults<Subject> subjects;

    public SubjectRepository() {
        realm = Realm.getDefaultInstance();
        subjects = realm.where(Subject.class).findAll();
    }

    public MutableLiveData<List<Subject>> getAllSubjects() {
        return new MutableLiveData<>(subjects);
    }

    public void addSubject(Subject subject) {
        realm.beginTransaction();
        realm.copyToRealm(subject);
        realm.commitTransaction();
    }

    public Subject changeSubjectTitle(Subject subject, String nTitle) {
        realm.beginTransaction();
        subject.setTitle(nTitle);
        realm.commitTransaction();
        return subject;
    }

    public void deleteSubject(Subject subject) {
        realm.beginTransaction();
        for (Section section : subject.getSectionList()) {
            section.getTaskList().deleteAllFromRealm();
            section.getTaskDoneList().deleteAllFromRealm();
        }
        subject.getSectionList().deleteAllFromRealm();
        subject.deleteFromRealm();
        realm.commitTransaction();
    }

    public Subject addSection(Subject subject, Section nSection) {
        realm.beginTransaction();
        subject.getSectionList().add(nSection);
        realm.commitTransaction();
        return subject;
    }

    public Section changeSectionTitle(Section section, String nTitle) {
        realm.beginTransaction();
        section.setTitle(nTitle);
        realm.commitTransaction();
        return section;
    }

    public void deleteSection(Section section) {
        realm.beginTransaction();
        section.getTaskList().deleteAllFromRealm();
        section.getTaskDoneList().deleteAllFromRealm();
        section.deleteFromRealm();
        realm.commitTransaction();
    }

    public Section addTask(Section section, Task nTask) {
        realm.beginTransaction();
        section.getTaskList().add(nTask);
        realm.commitTransaction();
        return section;
    }

    public Section addTask(Section section, Task nTask, int position){
        realm.beginTransaction();
        section.getTaskList().add(position, nTask);
        realm.commitTransaction();
        return section;
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

    public void updateTask(Task task) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(task);
        realm.commitTransaction();
    }

    public void deleteTask(Task task) {
        realm.beginTransaction();
        task.deleteFromRealm();
        realm.commitTransaction();
    }

    public Section deleteAllCompletedTasks(Section section) {
        realm.beginTransaction();
        section.getTaskDoneList().deleteAllFromRealm();
        realm.commitTransaction();
        return section;
    }

    public Section deleteTask(Section section, int position) {
        realm.beginTransaction();
        section.getTaskList().get(position).deleteFromRealm();
        realm.commitTransaction();
        return section;
    }

    public Section setTaskDone(Section section, int position) {
        realm.beginTransaction();
        Task task = section.getTaskList().get(position);
        section.getTaskList().remove(position);
        task.setAlarmStringId("");
        section.getTaskDoneList().add(task);
        realm.commitTransaction();
        return section;
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

    public Section setTaskUndone(Section section, int position) {
        realm.beginTransaction();
        Task task = section.getTaskDoneList().get(position);
        section.getTaskDoneList().remove(position);
        section.getTaskList().add(task);
        realm.commitTransaction();
        return section;
    }

}
