package com.themarto.etudetask;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    public final static int NOTIFICATION_ID = 0;
    public final static String TASK_ID = "task_id";
    public final static String SECTION_ID = "section_id";
    public final static String ACTION_TASK_DONE = "com.android.etudetask.ACTION_TASK_DONE";
    public final static String TASK_GROUP = "com.android.etudetask.TASK_GROUP";

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
        String taskId = getInputData().getString(TASK_ID);
        String sectionId = getInputData().getString(SECTION_ID);

        SubjectRepository.setAlarmTaskDone(taskId);

        lunchNotification(title, detail, taskId, sectionId);

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
    private void lunchNotification(String title, String detail, String taskId, String sectionId){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_etude_notification)
                .setColor(context.getResources().getColor(R.color.black))
                .setGroup(TASK_GROUP) // todo: group notifications
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
        doneIntent.putExtra(TASK_ID, taskId);
        doneIntent.putExtra(SECTION_ID, sectionId);
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context,
                0, doneIntent, PendingIntent.FLAG_ONE_SHOT);

        // Set the intent that will fire when the user taps the notification
        builder.setContentIntent(pendingIntent);

        builder.addAction(R.drawable.ic_done, "Done", donePendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Receiver when done button is clicked
    public static class AlarmReceiver extends BroadcastReceiver {

        SubjectRepository mRepository;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null){
                mRepository = new SubjectRepository();

                String taskId = intent.getExtras().getString(TASK_ID);
                String sectionId = intent.getExtras().getString(SECTION_ID);

                mRepository.setTaskDone(taskId, sectionId);

                NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
            }
        }
    }
}
