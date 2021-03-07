package com.themarto.etudetask.data;

import android.app.Application;

import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class SubjectViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private MutableLiveData<Subject> subject = new MutableLiveData<>();

    public SubjectViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void loadSubject (String id) {
        subject.setValue(repository.getSubject(id));
    }

    public void reloadSubject () {
        if (subject.getValue() != null) {
            subject.setValue(repository.getSubject(subject.getValue().getId()));
        }
    }

    public LiveData<Subject> getSubject () {
        return subject;
    }

    public void updateSubject (Subject updatedSubject) {
        subject.setValue(updatedSubject);
    }

    public void deleteSubject () {
        removeNotificationsByTag(subject.getValue().getId());
        repository.deleteSubject(subject.getValue());
    }

    public void deletedCompletedTasks () {
        // todo: check db
        Subject tempSubject = subject.getValue();
        List<Task> doneTasks = new ArrayList<>();
        for (Task t : tempSubject.getTaskList()) {
            if (t.isDone()) doneTasks.add(t);
        }
        tempSubject.getTaskList().removeAll(doneTasks);
        subject.setValue(tempSubject);
    }

    public void setTaskAsDone (Task task) {
        removeTaskNotifications(task);
        task.setDone(true);
        subject.setValue(subject.getValue());
    }

    public void deleteTask (Task task) {
        // todo: check on db, check returned value
        removeTaskNotifications(task);
        Subject tempSubject = subject.getValue();
        tempSubject.getTaskList().remove(task);
        subject.setValue(tempSubject);
    }

    public void commitChanges () {
        repository.updateSubject(subject.getValue());
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
