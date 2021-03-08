package com.themarto.etudetask.data;

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
    private MutableLiveData<Task> task = new MutableLiveData<>();

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void loadTask (String id) {
        task.setValue(repository.getTask(id));
    }

    public LiveData<Task> getTask () {
        return task;
    }

    public void updateTask (Task updatedTask) {
        task.setValue(updatedTask);
    }

    public void deleteTask () {
        removeTaskNotifications(task.getValue());
        repository.deleteTask(task.getValue());
    }

    public void commitTaskChanges () {
        repository.updateTask(task.getValue());
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
