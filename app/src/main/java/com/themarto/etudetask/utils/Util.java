package com.themarto.etudetask.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.themarto.etudetask.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.work.Data;

public class Util {

    public static final String SELECTED_SUBJECT_KEY = "SELECTED_SUBJECT";

    // todo: comment
    public static String getDateString(Date time) {
        Calendar myTime = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();
        String strDate;
        myTime.setTime(time);

        if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH)){
            strDate = "Today";
        } else if (myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH) - 1){
            strDate = "Yesterday";
        }
        else if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH) + 1){
            strDate = "Tomorrow";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
            strDate = format.format(time);
        }
        return strDate;
    }

    // todo: comment
    public static String getTimeString(Date time){
        String srtTime;
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
        srtTime = format.format(time);
        return  srtTime;
    }

    public static Data saveNotificationData(String title, String details, String taskId, String sectionId){
        return new Data.Builder()
                .putString("title", title)
                .putString("detail", details)
                .putString("task_id", taskId)
                .putString("section_id", sectionId)
                .build();
    }

    public static List<Task> getDoneTasks(List<Task> tasks){
        List<Task> doneTasks = new ArrayList<Task>();
        for(Task task : tasks) {
            if(task.isDone())
                doneTasks.add(task);
        }
        return doneTasks;
    }

    public static List<Task> getToDoTasks(List<Task> tasks){
        List<Task> toDoTasks = new ArrayList<Task>();
        for(Task task : tasks) {
            if(!task.isDone())
                toDoTasks.add(task);
        }
        return toDoTasks;
    }
}