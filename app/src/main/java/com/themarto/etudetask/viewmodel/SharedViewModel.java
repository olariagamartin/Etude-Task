package com.themarto.etudetask.viewmodel;

import com.themarto.etudetask.SignatureRepository;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private SignatureRepository mRepository;
    private MutableLiveData<List<Signature>> signatures;
    private MutableLiveData<Signature> selectedSignature = new MutableLiveData<>();
    private MutableLiveData<Chapter> selectedChapter = new MutableLiveData<>();
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public SharedViewModel() {
        mRepository = new SignatureRepository();
        signatures = new MutableLiveData<>();
        loadSignatures();
    }

    public LiveData<List<Signature>> getAllSignatures () {
        return signatures;
    }

    public void setStartSignature(int position) {
        selectedSignature.setValue(signatures.getValue().get(position));
    }

    public void loadSignatures(){
        signatures = mRepository.getAllSignatures();
        if(signatures.getValue().isEmpty()) {
            mRepository.addSignature(new Signature("Default")); // TODO: string
            selectSignature(0);
        }
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

    // CRUD actions
    public void addSignature(Signature newSignature) {
        mRepository.addSignature(newSignature);
        selectSignature(signatures.getValue().size() - 1);
    }

    /**
     * Change the title of the selected signature
     * @param title new title for the signature
     */
    public void changeSignatureTitle (String title) {
        Signature signature = mRepository.changeSignatureTitle(selectedSignature.getValue(), title);
        selectedSignature.setValue(signature);
    }

    /**
     * Delete the selected signature
     */
    public boolean deleteSignature () {
        if (signatures.getValue().size() > 1) {
            mRepository.deleteSignature(selectedSignature.getValue());
            selectedSignature.setValue(signatures.getValue().get(0));
            return true;
        }
        return false;
    }

    /**
     * Add a new Chapter to the selected signature
     * @param chapter new chapter to be added
     */
    public void addChapter (Chapter chapter) {
        Signature signature = mRepository.addChapter(selectedSignature.getValue(), chapter);
        selectedSignature.setValue(signature);
    }

    /**
     * Change the title of the selected chapter
     * @param title new title of the chapter
     */
    public void changeChapterTitle (String title) {
        Chapter chapter = mRepository.changeChapterTitle(selectedChapter.getValue(), title);
        selectedChapter.setValue(chapter);
    }

    /**
     * Delete the selected chapter
     */
    public void deleteChapter () {
        mRepository.deleteChapter(selectedChapter.getValue());
    }

    /**
     * Add a new task to the selected chapter
     * @param task new task to be added
     */
    public void addTask (Task task) {
        Chapter chapter = mRepository.addTask(selectedChapter.getValue(), task);
        selectedChapter.setValue(chapter);
    }

    /**
     * Change the task title of the selected task
     * @param title new title of the task
     */
    public void changeTaskTitle (String title) {
        Task task = mRepository.changeTaskTitle(selectedTask.getValue(), title);
        selectedTask.setValue(task);
    }

    /**
     * Delete selected task
     */
    public void deleteTask () {
        mRepository.deleteTask(selectedTask.getValue());
    }

}
