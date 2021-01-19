package com.themarto.etudetask.data;

import android.app.Application;

import com.themarto.etudetask.data.SubjectRepository;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Subtask;
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
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public SharedViewModel(Application application) {
        super(application);
        mRepository = new SubjectRepository();
        subjects = new MutableLiveData<>();
        loadSubjects();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return subjects;
    }

    public void loadSubjects() {
        subjects.setValue(mRepository.getAllSubjects());
    }

    public void selectSubject(int position) {
        selectedSubject.setValue(subjects.getValue().get(position));
    }

    public LiveData<Subject> getSelectedSubject() {
        return selectedSubject;
    }

    public void selectTask(int position) {
        // selectedTask.setValue(task);
    }

    public void selectTask(Task task){
        selectedTask.setValue(task);
    }

    public LiveData<Task> getSelectedTask() {
        return selectedTask;
    }

    // CRUD actions
    public void addSubject(Subject newSubject) {
        mRepository.addSubject(newSubject);
        loadSubjects();
    }

    public void updateSubject(Subject subject){
        Subject updateSubject = mRepository.updateSubject(subject);
        selectedSubject.setValue(updateSubject);
        loadSubjects();
    }

    public void deleteSubject() {
        removeNotificationsByTag(selectedSubject.getValue().getId());
        mRepository.deleteSubject(selectedSubject.getValue());
    }

    public void deleteSubject(int position){
        Subject subject = subjects.getValue().get(position);
        removeNotificationsByTag(subject.getId());
        mRepository.deleteSubject(subject);
        loadSubjects();
    }

    public void addTask(Task task) {
        Subject subject = mRepository.addTask(selectedSubject.getValue(), task);
        selectedSubject.setValue(subject);
    }

    public void addTask(Task task, int position) {
        mRepository.addTask(selectedSubject.getValue(), task, position);
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void updateTask(Task task) {
        mRepository.updateTask(task);
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public Task deleteTask() {
        removeTaskNotifications(selectedTask.getValue());
        Task deletedTask = mRepository.deleteTask(selectedTask.getValue());
        selectedSubject.setValue(selectedSubject.getValue());
        return deletedTask;
    }

    public void deleteAllCompletedTasks() {
        // Section section = mRepository.deleteAllCompletedTasks(selectedSection.getValue());
        // selectedSection.setValue(section);
    }

    public Task deleteTask(Task task) {
        removeTaskNotifications(task);
        Task deletedTask = mRepository.deleteTask(task);
        selectedSubject.setValue(selectedSubject.getValue());
        return deletedTask;
    }

    public void setTaskDone(Task task) {
        removeNotificationsByTag(task.getAlarmStringId());
        mRepository.setTaskDone(task);
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void setTaskUndone(Task task) {
        mRepository.setTaskUndone(task);
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void addSubtask(Subtask subtask){
        Task task = mRepository.addSubtask(selectedTask.getValue(), subtask);
        selectedTask.setValue(task);
    }

    private void removeTaskNotifications(Task task) {
        if (task.hasAlarm()) {
            WorkManager.getInstance(getApplication()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
        }
    }

    private void removeNotificationsByTag(String tag) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(tag);
    }

}
