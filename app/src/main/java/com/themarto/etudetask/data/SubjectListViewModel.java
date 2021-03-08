package com.themarto.etudetask.data;

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
        loadSubjectList();
    }

    public void loadSubjectList () {
        subjectListLiveData.setValue(repository.getAllSubjects());
    }

    public void reloadSubjectList () {
        subjectListLiveData.setValue(subjectListLiveData.getValue());
    }

    public LiveData<List<Subject>> getSubjectList () {
        return subjectListLiveData;
    }

    public void deleteSubject (int position) {
        List<Subject> subjectList = subjectListLiveData.getValue();
        removeNotificationsByTag(subjectList.get(position).getId());
        repository.deleteSubject(subjectList.get(position));
        subjectList.remove(position);
        subjectListLiveData.setValue(subjectList);
    }

    public void commitChanges () {
        repository.updateSubjectList(subjectListLiveData.getValue());
    }

    private void removeNotificationsByTag(String tag) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(tag);
    }

    // todo: add on cleared
}
