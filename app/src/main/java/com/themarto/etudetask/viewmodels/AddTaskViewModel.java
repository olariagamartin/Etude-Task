package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class AddTaskViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private Subject subject;

    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void loadSubject (String id) {
        subject = repository.getSubject(id);
    }

    public Subject getSubject () {
        return subject;
    }

    public void addTask (Task task) {
        subject.getTaskList().add(task);
        repository.updateSubject(subject);
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
