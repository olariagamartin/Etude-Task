package com.themarto.etudetask;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static void hideSoftKeyboard (View view, Context context){
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String getDateString(Date time) {
        Calendar myTime = Calendar.getInstance();
        Calendar actual = Calendar.getInstance();
        String strDate;
        myTime.setTime(time);
        if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH)){
            strDate = "Today";
        } else if(myTime.get(Calendar.DAY_OF_MONTH) == actual.get(Calendar.DAY_OF_MONTH) + 1){
            strDate = "Tomorrow";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
            strDate = format.format(time);
        }
        return strDate;
    }

    public static String getTimeString(Date time){
        String srtTime = "";
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
        srtTime = format.format(time);
        return  srtTime;
    }

    public interface MyListener {
        void onItemClick(int position);
    }
}