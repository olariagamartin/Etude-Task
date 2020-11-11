package com.themarto.etudetask.viewmodel;

import com.themarto.etudetask.Util;
import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Signature>> signatures;
    private MutableLiveData<Signature> selectedSignature = new MutableLiveData<>();
    private MutableLiveData<Chapter> selectedChapter = new MutableLiveData<>();
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public SharedViewModel() {
        signatures = new MutableLiveData<>();
        loadSignatures();
        setStartSignature();
    }

    public LiveData<List<Signature>> getAllSignatures () {
        if (signatures == null){
            loadSignatures();
        }
        return signatures;
    }

    public void loadSignatures(){
        // TODO: connect to DB
        signatures.setValue(Util.getSignatureListEx());
    }

    private void setStartSignature() {
        // TODO:
        selectedSignature.setValue(signatures.getValue().get(0));
    }

    public void selectSignature (int position) {
        selectedSignature.setValue(signatures.getValue().get(position));
    }

    public LiveData<Signature> getSelectedSignature () {
        return selectedSignature;
    }

    public void selectChapter (int position) {
        Chapter chapter = selectedSignature.getValue().getChapterList().get(position);
        selectedChapter.setValue(chapter);
    }

    public LiveData<Chapter> getSelectedChapter () {
        return  selectedChapter;
    }

    public void selectTask (int position) {
        Task task = selectedChapter.getValue().getTaskList().get(position);
        selectedTask.setValue(task);
    }

    public LiveData<Task> getSelectedTask () {
        return selectedTask;
    }

    // Create
    public void addSignature(Signature newSignature) {
        // TODO: send to DB
    }

}
