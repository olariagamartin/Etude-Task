package com.themarto.etudetask.utils;

import android.content.Context;
import android.util.TypedValue;

import com.themarto.etudetask.R;
import com.themarto.etudetask.notification.WorkManagerAlarm;
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

    public static int resolveColorAttr(int colorAttr, Context context) {
        TypedValue resolvedAttr = resolveThemeAttr(colorAttr, context);
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        int colorRes = (resolvedAttr.resourceId != 0) ? resolvedAttr.resourceId : resolvedAttr.data;
        return ContextCompat.getColor(context, colorRes);
    }

    public static boolean isDayPassed(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        return (today.get(Calendar.YEAR) >= day.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) > day.get(Calendar.DAY_OF_YEAR));
    }

    private static TypedValue resolveThemeAttr(int attrRes, Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

    public static String getDateStr(Date date, Context context, boolean hasTime) {
        String strDate = "";
        if (date != null) {
            if (hasTime) {
                strDate = Util.getDateString(date, context) + ", " + Util.getTimeString(date);
            } else {
                strDate = Util.getDateString(date, context);
            }
        }
        return strDate;
    }

    /**
     * Return the date in format "Day, Month"
     *
     * @param time the date to get day and month
     * @return formatted string
     */
    public static String getDateString(Date time, Context context) {
        Calendar myTime = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();
        String strDate = "";
        myTime.setTime(time);

        if (isToday(time)) {
            strDate = context.getString(R.string.date_format_today);
        } else if (isYesterday(time)) {
            strDate = context.getString(R.string.date_format_yesterday);
        } else if (isTomorrow(time)) {
            strDate = context.getString(R.string.date_format_tomorrow);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
            strDate = format.format(time);
        }

        return strDate;
    }

    private static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        return today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isYesterday(Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        return yesterday.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isTomorrow(Date date) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        return tomorrow.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Return time in format "5:35 PM"
     *
     * @param time the day to get hour and minute
     * @return formatted time
     */
    public static String getTimeString(Date time) {
        String srtTime;
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
        srtTime = format.format(time);
        return srtTime;
    }

    public static Data saveNotificationData(String title, String details, String taskId) {
        return new Data.Builder()
                .putString(WorkManagerAlarm.DATA_KEY_TITLE, title)
                .putString(WorkManagerAlarm.DATA_KEY_DETAIL, details)
                .putString(WorkManagerAlarm.TASK_ID, taskId)
                .build();
    }

    public static List<Task> getDoneTasks(List<Task> tasks) {
        List<Task> doneTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.isDone())
                doneTasks.add(task);
        }
        return doneTasks;
    }

    public static List<Task> getToDoTasks(List<Task> tasks) {
        List<Task> toDoTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (!task.isDone())
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