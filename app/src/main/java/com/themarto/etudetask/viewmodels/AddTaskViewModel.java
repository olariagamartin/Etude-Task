package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class AddTaskViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private Subject subjectLiveData;

    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void loadSubject (String id) {
        subjectLiveData = repository.getSubject(id);
    }

    public Subject getSubject () {
        return subjectLiveData;
    }

    public void addTask (Task task) {
        subjectLiveData.getTaskList().add(task);
        repository.updateSubject(subjectLiveData);
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
