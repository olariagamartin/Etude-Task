package com.themarto.etudetask.notification;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.google.android.material.color.MaterialColors;
import com.themarto.etudetask.MainActivity;
import com.themarto.etudetask.R;
import com.themarto.etudetask.SubjectRepository;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MyNotificationManager {

    public final static String TASK_ID = "task_id";
    public final static String ACTION_TASK_DONE = "com.android.etudetask.ACTION_TASK_DONE";
    public final static String TASK_GROUP = "com.android.etudetask.TASK_GROUP";

    private final String CHANNEL_ID = "android.EtudeTask.CHANNEL_ID";
    private Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
    }

    // id is for update or cancel the notification in the future
    public void lunchNotification(String title, String detail, String taskId){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_etude_notification)
                .setColor(ContextCompat.getColor(context, R.color.blue_500))
                //.setGroup(TASK_GROUP)
                .setContentTitle(title) // task title
                .setContentText(detail) // subject title
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        // when click back button the application closes
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("task_id", taskId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the intent that will fire when the user taps the notification
        builder.setContentIntent(pendingIntent);

        // Done button
        Intent doneIntent = new Intent(context, AlarmReceiver.class);
        doneIntent.putExtra(TASK_ID, taskId);
        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context,
                0, doneIntent, PendingIntent.FLAG_ONE_SHOT);

        builder.addAction(R.drawable.ic_done, context.getString(R.string.notification_done_button), donePendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(taskId.hashCode(), builder.build());
    }

    private void createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Notifications";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Receiver when done button is clicked
    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null){

                String taskId = intent.getExtras().getString(TASK_ID);

                SubjectRepository.setTaskDone(taskId);

                NotificationManagerCompat.from(context).cancel(taskId.hashCode());
            }
        }
    }
}
