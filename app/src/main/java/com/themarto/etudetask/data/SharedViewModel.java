package com.themarto.etudetask.data;

import android.app.Application;

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
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();
    private MutableLiveData<List<Task>> todayTaskList = new MutableLiveData<>();

    public SharedViewModel(Application application) {
        super(application);
        mRepository = new SubjectRepository();
        subjects = new MutableLiveData<>();
        loadSubjects();
    }

    public LiveData<List<Subject>> getAllSubjects() {
        return subjects;
    }

    public LiveData<List<Task>> getTodayTaskList () {
        if (todayTaskList.getValue() == null) {
            todayTaskList.setValue(mRepository.getTodayTaskList());
        }
        return todayTaskList;
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

    public void selectTask(Task task){
        Task unmanagedTask = task.getRealm().copyFromRealm(task);
        selectedTask.setValue(unmanagedTask);
    }

    public void selectTask(String taskId) {
        Task unmanagedTask = mRepository.getTask(taskId);
        selectedTask.setValue(unmanagedTask);
    }

    public LiveData<Task> getSelectedTask() {
        return selectedTask;
    }

    // Create, Update, Delete
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
        selectedTask.setValue(task);
    }

    public void commitTaskChanges(){
        mRepository.updateTask(selectedTask.getValue());
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void deleteTask() {
        removeTaskNotifications(selectedTask.getValue());
        mRepository.deleteTask(selectedTask.getValue());
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void deleteAllCompletedTasks() {
        Subject subject = mRepository.deleteAllCompletedTasks(selectedSubject.getValue());
        selectedSubject.setValue(subject);
    }

    public Task deleteTask(Task task) {
        removeTaskNotifications(task);
        Task deletedTask = mRepository.deleteTask(task);
        selectedSubject.setValue(selectedSubject.getValue());
        return deletedTask;
    }

    public void setTaskDone(Task task) {
        removeTaskNotifications(task);
        mRepository.setTaskDone(task);
        selectedSubject.setValue(selectedSubject.getValue());
    }

    public void setTaskUndone(Task task) {
        mRepository.setTaskUndone(task);
        selectedSubject.setValue(selectedSubject.getValue());
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

    public void closeDB(){
        mRepository.closeRealm();
    }
}
