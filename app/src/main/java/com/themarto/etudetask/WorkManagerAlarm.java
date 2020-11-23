package com.themarto.etudetask;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkManagerAlarm extends Worker {

    public final static String DATA_KEY_TITLE = "title";
    public final static String DATA_KEY_DETAIL = "detail";
    public final static String DATA_KEY_ID = "id";

    private String CHANNEL_ID = "android.EtudeTask.CHANNEL_ID";
    private static Context context;

    public WorkManagerAlarm(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString(DATA_KEY_TITLE);
        String detail = getInputData().getString(DATA_KEY_DETAIL);
        int id = (int) getInputData().getLong(DATA_KEY_ID, 0);

        lunchNotification(title, detail, id);

        return Result.success();
    }

    public static void saveAlarm(long duration, Data data, String tag){
        OneTimeWorkRequest alarm = new OneTimeWorkRequest.Builder(WorkManagerAlarm.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag)
                .setInputData(data).build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueue(alarm);
    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void lunchNotification(String title, String detail, int id){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_etude_notification)
                .setContentTitle(title)
                .setContentText(detail)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // todo: intent to fragment
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        // when click back button the application closes
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Set the intent that will fire when the user taps the notification
        builder.setContentIntent(pendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.notify(id, builder.build());
    }
}
