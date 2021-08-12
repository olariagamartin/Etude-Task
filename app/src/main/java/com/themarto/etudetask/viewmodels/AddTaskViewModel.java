package com.themarto.etudetask.viewmodels;

import android.app.Application;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.Calendar;
import java.util.Date;

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
    private MutableLiveData<String> flagRgbColor = new MutableLiveData<>();
    private MutableLiveData<Boolean> dateSet = new MutableLiveData<>();
    private MutableLiveData<Boolean> timeSet = new MutableLiveData<>();

    private Calendar taskCalendar = Calendar.getInstance();

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

    public LiveData<String> getFlagRgbColor() {
        return flagRgbColor;
    }

    public LiveData<Boolean> isDateSet () {
        return dateSet;
    }

    public LiveData<Boolean> isTimeSet () {
        return timeSet;
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
        // todo-issue: when the fragment is recreated the
        // will be on the add details edit text
        addDetailsClicked.setValue(true);
    }

    public void onFlagColorSelected (String rgbColor) {
        flagRgbColor.setValue(rgbColor);
        task.setFlagColor(rgbColor);
    }

    public void onDateSet(int dayOfMonth, int month, int year) {
        taskCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        taskCalendar.set(Calendar.MONTH, month);
        taskCalendar.set(Calendar.YEAR, year);
        dateSet.setValue(true);
    }

    public Date getTaskTime () {
        return taskCalendar.getTime();
    }

    public void onTimeSet (int hourOfDay, int minute) {
        taskCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        taskCalendar.set(Calendar.MINUTE, minute);
        taskCalendar.set(Calendar.SECOND, 0);
        timeSet.setValue(true);
    }

    public long getTaskTimeInMillis () {
        return taskCalendar.getTimeInMillis();
    }

    public void onRemoveTime () {
        timeSet.setValue(false);
    }

    public void onRemoveDate() {
        dateSet.setValue(false);
        timeSet.setValue(false);
    }

    @Override
    protected void onCleared() {
        repository.closeRealm();
        super.onCleared();
    }
}
