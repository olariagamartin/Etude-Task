package com.themarto.etudetask.utils;

import android.content.Context;
import android.util.TypedValue;

import com.themarto.etudetask.R;
import com.themarto.etudetask.WorkManagerAlarm;
import com.themarto.etudetask.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.work.Data;

public class Util {

    public static final String SELECTED_SUBJECT_KEY = "SELECTED_SUBJECT";

    public static int resolveColorAttr (int colorAttr, Context context) {
        TypedValue resolvedAttr = resolveThemeAttr(colorAttr, context);
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        int colorRes = (resolvedAttr.resourceId != 0) ? resolvedAttr.resourceId : resolvedAttr.data;
        return ContextCompat.getColor(context, colorRes);
    }

    private static TypedValue resolveThemeAttr (int attrRes, Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

    public static String getDateStr(Date date, Context context, boolean hasTime) {
        String strDate = "";
        if (date != null){
            if(hasTime) {
                strDate = Util.getDateString(date, context) + ", " + Util.getTimeString(date);
            } else{
                strDate = Util.getDateString(date, context);
            }
        }
        return strDate;
    }

    /**
     * Return the date in format "Day, Month"
     * @param time the date to get day and month
     * @return formatted string
     */
    public static String getDateString(Date time, Context context) {
        Calendar myTime = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();
        String strDate = "";
        myTime.setTime(time);

        if (myTime.get(Calendar.MONTH) == actual.get(Calendar.MONTH)
                && myTime.get(Calendar.YEAR) == actual.get(Calendar.YEAR)) {
            if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH)){
                strDate = context.getString(R.string.date_format_today);
            } else if (myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH) - 1){
                strDate = context.getString(R.string.date_format_yesterday);
            }
            else if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH) + 1){
                strDate = context.getString(R.string.date_format_tomorrow);
            }
            else {
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
                strDate = format.format(time);
            }
        }
        else {
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
                .putString(WorkManagerAlarm.DATA_KEY_TITLE, title)
                .putString(WorkManagerAlarm.DATA_KEY_DETAIL, details)
                .putString(WorkManagerAlarm.TASK_ID, taskId)
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

    public static class FlagColors {
        public static String RED = "#FF1744";
        public static String YELLOW = "#FFC400";
        public static String BLUE = "#2979FF";
        public static String NONE = "#000000";
    }
}