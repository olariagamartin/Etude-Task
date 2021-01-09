package com.themarto.etudetask;

import android.content.Context;

import com.themarto.etudetask.data.SubjectRepository;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkManagerAlarm extends Worker {

    public final static String DATA_KEY_TITLE = "title";
    public final static String DATA_KEY_DETAIL = "detail";
    public final static String TASK_ID = "task_id";
    public final static String SECTION_ID = "section_id";

    private Context context;

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

        MyNotificationManager manager = new MyNotificationManager(context);
        manager.lunchNotification(title, detail, taskId, sectionId);

        return Result.success();
    }

    public static String saveAlarm(long duration, Data data, String tag1, String tag2, Context context){
        OneTimeWorkRequest alarm = new OneTimeWorkRequest.Builder(WorkManagerAlarm.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag1).addTag(tag2)
                .setInputData(data).build();
        String alarmStringId = alarm.getId().toString();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueue(alarm);
        return alarmStringId;
    }

}
