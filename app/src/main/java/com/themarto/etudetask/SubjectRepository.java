package com.themarto.etudetask;

import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.realm.Realm;
import io.realm.RealmResults;

public class SubjectRepository {

    private Realm realm;

    private MutableLiveData<List<Subject>> allSubjects;

    public SubjectRepository() {
        realm = Realm.getDefaultInstance();
        allSubjects = new MutableLiveData<>();
        RealmResults<Subject> subjects = realm.where(Subject.class).findAll();
        allSubjects.setValue(subjects);
    }

    public MutableLiveData<List<Subject>> getAllSubjects() {
        return allSubjects;
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
        section.deleteFromRealm();
        realm.commitTransaction();
    }

    public Section addTask(Section section, Task nTask) {
        realm.beginTransaction();
        section.getTaskList().add(nTask);
        realm.commitTransaction();
        return section;
    }

    public void updateTaskTitle(Task task, String nTitle) {
        realm.beginTransaction();
        task.setTitle(nTitle);
        realm.commitTransaction();
    }

    public void updateTaskDetails (Task task, String details) {
        realm.beginTransaction();
        task.setDetails(details);
        realm.commitTransaction();
    }

    public void deleteTask(Task task) {
        realm.beginTransaction();
        task.deleteFromRealm();
        realm.commitTransaction();
    }

    public Section deleteAllCompletedTasks(Section section) {
        realm.beginTransaction();
        section.getTaskDoneList().clear();
        realm.commitTransaction();
        return section;
    }

    public Section deleteTask(Section section, int position){
        realm.beginTransaction();
        section.getTaskList().get(position).deleteFromRealm();
        realm.commitTransaction();
        return section;
    }

    public Section setTaskDone (Section section, int position) {
        realm.beginTransaction();
        Task task = section.getTaskList().get(position);
        section.getTaskList().remove(position);
        section.getTaskDoneList().add(task);
        realm.commitTransaction();
        return section;
    }

    public Section setTaskUndone (Section section, int position) {
        realm.beginTransaction();
        Task task = section.getTaskDoneList().get(position);
        section.getTaskDoneList().remove(position);
        section.getTaskList().add(task);
        realm.commitTransaction();
        return section;
    }

}