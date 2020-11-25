package com.themarto.etudetask.viewmodel;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.List;
import java.util.UUID;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class SharedViewModel extends AndroidViewModel {
    private SubjectRepository mRepository;
    private MutableLiveData<List<Subject>> subjects;
    private MutableLiveData<Subject> selectedSubject = new MutableLiveData<>();
    private MutableLiveData<Section> selectedSection = new MutableLiveData<>();
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public SharedViewModel(Application application) {
        super(application);
        mRepository = new SubjectRepository();
        subjects = new MutableLiveData<>();
        loadSubjects();
    }

    public LiveData<List<Subject>> getAllSubjects () {
        return subjects;
    }

    public void setStartSubject(int position) {
        selectedSubject.setValue(subjects.getValue().get(position));
    }

    public void loadSubjects(){
        subjects = mRepository.getAllSubjects();
        if(subjects.getValue().isEmpty()) {
            mRepository.addSubject(new Subject("Default")); // TODO: string
            selectSubject(0);
        }
    }

    public void selectSubject (int position) {
        selectedSubject.setValue(subjects.getValue().get(position));
    }

    public LiveData<Subject> getSelectedSubject () {
        return selectedSubject;
    }

    public void selectSection (int position) {
        Section section = selectedSubject.getValue().getSectionList().get(position);
        selectedSection.setValue(section);
    }

    public LiveData<Section> getSelectedSection () {
        return  selectedSection;
    }

    public void selectTask (int position) {
        Task task = selectedSection.getValue().getTaskList().get(position);
        selectedTask.setValue(task);
    }

    public LiveData<Task> getSelectedTask () {
        return selectedTask;
    }

    // CRUD actions
    public void addSubject(Subject newSubject) {
        mRepository.addSubject(newSubject);
        selectSubject(subjects.getValue().size() - 1);
    }

    public void changeSubjectTitle (String title) {
        Subject subject = mRepository.changeSubjectTitle(selectedSubject.getValue(), title);
        selectedSubject.setValue(subject);
    }

    public boolean deleteSubject () {
        if (subjects.getValue().size() > 1) {
            removeNotificationsByTag(selectedSubject.getValue().getId());
            mRepository.deleteSubject(selectedSubject.getValue());
            selectedSubject.setValue(subjects.getValue().get(0));
            return true;
        }
        return false;
    }

    public void addSection (Section section) {
        Subject subject = mRepository.addSection(selectedSubject.getValue(), section);
        selectedSubject.setValue(subject);
    }

    public void changeSectionTitle (String title) {
        Section section = mRepository.changeSectionTitle(selectedSection.getValue(), title);
        selectedSection.setValue(section);
    }

    public void deleteSection () {
        removeNotificationsByTag(selectedSection.getValue().getId());
        mRepository.deleteSection(selectedSection.getValue());
    }

    public void addTask (Task task) {
        // maybe I could try to set the notifications here
        Section section = mRepository.addTask(selectedSection.getValue(), task);
        selectedSection.setValue(section);
    }

    public void updateTask(Task task){
        mRepository.updateTask(task);
    }

    public void deleteTask () {
        removeTaskNotifications(selectedTask.getValue());
        mRepository.deleteTask(selectedTask.getValue());
    }

    public void deleteAllCompletedTasks () {
        Section section = mRepository.deleteAllCompletedTasks(selectedSection.getValue());
        selectedSection.setValue(section);
    }

    public void deleteTask (int position) {
        removeTaskNotifications(selectedSection.getValue().getTaskList().get(position));
        Section section = mRepository.deleteTask(selectedSection.getValue(), position);
        selectedSection.setValue(section);
    }

    public void setTaskDone (int position) {
        removeTaskNotifications(selectedSection.getValue().getTaskList().get(position));
        Section section = mRepository.setTaskDone(selectedSection.getValue(), position);
        selectedSection.setValue(section);
    }

    public void setTaskUndone (int position) {
        Section section = mRepository.setTaskUndone(getSelectedSection().getValue(), position);
        selectedSection.setValue(section);
    }

    private void removeTaskNotifications(Task task){
        if (task.hasAlarm()){
            WorkManager.getInstance(getApplication()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
        }
    }

    private void removeNotificationsByTag(String tag) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(tag);
    }

}
