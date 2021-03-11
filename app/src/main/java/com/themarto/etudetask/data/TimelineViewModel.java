package com.themarto.etudetask.data;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

public class TimelineViewModel extends AndroidViewModel {

    private SubjectRepository repository;
    private MutableLiveData<List<Task>> todayTaskListLD = new MutableLiveData<>();
    private MutableLiveData<List<Task>> upcomingTaskListLD = new MutableLiveData<>();

    public TimelineViewModel(@NonNull Application application) {
        super(application);
        repository = new SubjectRepository();
        loadTodayTaskList();
    }

    public void loadLists () {
        loadTodayTaskList();
        loadUpcomingTaskList();
    }

    public void loadTodayTaskList() {
        todayTaskListLD.setValue(repository.getTodayTaskList());
    }

    public void loadUpcomingTaskList () {
        upcomingTaskListLD.setValue(repository.getUpcomingTaskList());
    }

    public LiveData<List<Task>> getTodayTaskList() {
        return todayTaskListLD;
    }

    public LiveData<List<Task>> getUpcomingTaskList () {
        return upcomingTaskListLD;
    }

    public void setTaskAsDone (Task task) {
        removeTaskNotifications(task);
        task.setDone(true);
        task.setAlarmStringId("");
        repository.updateTask(task);
        loadLists();
    }

    public void addTask (Task task) {
        Subject subject = task.getSubject();
        subject.getTaskList().add(task);
        repository.updateSubject(subject);
        loadLists();
    }

    public void updateTask (Task task) {
        repository.updateTask(task);
        loadLists();
    }

    public void deleteTask (Task task) {
        // todo: check on db, check returned value
        removeTaskNotifications(task);
        task.setAlarmStringId("");
        repository.deleteTask(task);
        loadLists();
    }

    private void removeTaskNotifications(Task task) {
        if (task.hasAlarm()) {
            WorkManager.getInstance(getApplication()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
        }
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
