package com.themarto.etudetask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkManagerAlarm extends Worker {

    public final static String DATA_KEY_TITLE = "title";
    public final static String DATA_KEY_DETAIL = "detail";
    public final static String DATA_KEY_ID = "id";
    public final static String TASK_ID = "task_id";
    public final static String ACTION_TASK_DONE = "com.android.etudetask.ACTION_TASK_DONE";

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
        int id = getInputData().getInt(DATA_KEY_ID, 2);
        String taskId = getInputData().getString(TASK_ID);

        lunchNotification(title, detail, id, taskId);

        return Result.success();
    }

    public static String saveAlarm(long duration, Data data, String tag1, String tag2){
        OneTimeWorkRequest alarm = new OneTimeWorkRequest.Builder(WorkManagerAlarm.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag1).addTag(tag2)
                .setInputData(data).build();
        String alarmStringId = alarm.getId().toString();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueue(alarm);
        return alarmStringId;
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

    // id is for update or cancel the notification in the future
    private void lunchNotification(String title, String detail, int id, String taskId){
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

        context.registerReceiver(new AlarmReceiver(),
                new IntentFilter(ACTION_TASK_DONE));
        // Intent for done button
        Intent doneIntent = new Intent(ACTION_TASK_DONE);
        doneIntent.putExtra("task_id", taskId);
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context,
                0, doneIntent, PendingIntent.FLAG_ONE_SHOT);

        // Set the intent that will fire when the user taps the notification
        builder.setContentIntent(pendingIntent);

        builder.addAction(R.drawable.ic_done, "Done", donePendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());
    }

    // Receiver when done button is clicked
    public static class AlarmReceiver extends BroadcastReceiver {

        SubjectRepository mRepository;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null){
                String taskId = intent.getExtras().getString(TASK_ID);
                mRepository = new SubjectRepository();
                mRepository.setTaskDone(taskId);
                // todo: extract notification id in variable
                NotificationManagerCompat.from(context).cancel(1);
            }
        }
    }
}
