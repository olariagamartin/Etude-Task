package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class SubjectListViewModel extends AndroidViewModel {

    private MutableLiveData<List<Subject>> subjectListLiveData = new MutableLiveData<>();
    private SubjectRepository repository;

    public SubjectListViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
    }

    public void loadSubjectList() {
        subjectListLiveData.setValue(repository.getAllSubjects());
    }

    public LiveData<List<Subject>> getSubjectList () {
        loadSubjectList();
        return subjectListLiveData;
    }

    public void deleteSubject (Subject subject) {
        removeNotificationsByTag(subject.getId());
        repository.deleteSubject(subject);
        loadSubjectList();
    }

    public void addSubject (Subject subject) {
        repository.addSubject(subject);
        loadSubjectList();
    }

    public void updateSubject (Subject subject) {
        repository.updateSubject(subject);
        loadSubjectList();
    }

    private void removeNotificationsByTag(String tag) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(tag);
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
