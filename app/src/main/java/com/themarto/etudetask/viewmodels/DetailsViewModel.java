package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Task;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class DetailsViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private String taskId;
    private MutableLiveData<Task> taskLiveData = new MutableLiveData<>();

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void setTaskId (String id) {
        taskId = id;
        loadTask();
    }

    public void loadTask () {
        taskLiveData.setValue(repository.getTask(taskId));
    }

    public LiveData<Task> getTask () {
        return taskLiveData;
    }

    public void updateTask (Task updatedTask) {
        taskLiveData.setValue(updatedTask);
    }

    public void deleteTask () {
        removeTaskNotifications(taskLiveData.getValue());
        repository.deleteTask(taskLiveData.getValue());
    }

    public void commitTaskChanges () {
        repository.deleteSubtasks(taskLiveData.getValue());
        repository.updateTask(taskLiveData.getValue());
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
