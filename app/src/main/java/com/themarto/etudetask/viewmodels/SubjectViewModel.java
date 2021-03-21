package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class SubjectViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private MutableLiveData<Subject> subject = new MutableLiveData<>();
    private String subjectId;

    public SubjectViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    // this method is obligatory, maybe I can make a Factory class
    public void setSubjectId (String id) {
        this.subjectId = id;
        loadSubject();
    }

    public void loadSubject () {
        subject.setValue(repository.getSubject(subjectId));
    }

    public LiveData<Subject> getSubject () {
        return subject;
    }

    public void updateSubject (Subject updatedSubject) {
        repository.updateSubject(updatedSubject);
        loadSubject();
    }

    public void deleteSubject () {
        removeNotificationsByTag(subject.getValue().getId());
        repository.deleteSubject(subject.getValue());
        loadSubject();
    }

    public void addTask (Task task) {
        subject.getValue().getTaskList().add(task);
        repository.updateSubject(subject.getValue());
        loadSubject();
    }

    public void deletedCompletedTasks () {
        repository.deleteAllCompletedTasks(subject.getValue());
        loadSubject();
    }

    public void setTaskAsDone (Task task) {
        removeTaskNotifications(task);
        task.setDone(true);
        task.setAlarmStringId("");
        repository.updateTask(task);
        loadSubject();
    }

    public void updateTask (Task task) {
        repository.updateTask(task);
        loadSubject();
    }

    public void deleteTask (Task task) {
        // todo: check on db, check returned value
        removeTaskNotifications(task);
        task.setAlarmStringId("");
        repository.deleteTask(task);
        loadSubject();
    }

    private void removeNotificationsByTag(String tag) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(tag);
        repository.deleteSubject(subject.getValue());
    }

    private void removeTaskNotifications(Task task) {
        if (task.hasAlarm()) {
            WorkManager.getInstance(getApplication()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
        }
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
