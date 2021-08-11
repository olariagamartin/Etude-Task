package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddTaskViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private Subject subject;
    private Task task = new Task();

    private MutableLiveData<Boolean> saveBtnActive = new MutableLiveData<>();
    private MutableLiveData<Boolean> addDetailsClicked = new MutableLiveData<>();

    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public LiveData<Boolean> isSaveBtnActive() {
        return saveBtnActive;
    }

    public LiveData<Boolean> isAddDetailsClicked () {
        return addDetailsClicked;
    }

    public void loadSubject (String id) {
        subject = repository.getSubject(id);
        task.setSubject(subject);
    }

    public Subject getSubject () {
        return subject;
    }

    public void addTask (Task task) {
        subject.getTaskList().add(task);
        repository.updateSubject(subject);
    }

    public void onTaskTitleTextChanged (String title) {
        if (title.isEmpty()) {
            saveBtnActive.setValue(false);
        } else {
            saveBtnActive.setValue(true);
        }
        task.setTitle(title);
    }

    public void onAddDetailsClicked () {
        addDetailsClicked.setValue(true);
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
