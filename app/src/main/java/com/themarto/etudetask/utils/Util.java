package com.themarto.etudetask.utils;

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

    /**
     * Return the date in format "Day, Month"
     * @param time the date to get day and month
     * @return formatted string
     */
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

    /**
     * Return time in format "5:35 PM"
     * @param time the day to get hour and minute
     * @return formatted time
     */
    public static String getTimeString(Date time){
        String srtTime;
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
        srtTime = format.format(time);
        return  srtTime;
    }

    public static Data saveNotificationData(String title, String details, String taskId){
        return new Data.Builder()
                .putString("title", title)
                .putString("detail", details)
                .putString("task_id", taskId)
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