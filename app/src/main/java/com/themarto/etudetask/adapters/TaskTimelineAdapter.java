package com.themarto.etudetask.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.utils.Util;
import com.themarto.shadow.ShadowView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskTimelineAdapter extends RecyclerView.Adapter<TaskTimelineAdapter.ViewHolder> {

    private List<Task> taskList;

    private TaskAdapter.TaskListener listener;

    private Context context;

    private boolean showDate;

    public TaskTimelineAdapter(List<Task> taskList, TaskAdapter.TaskListener listener, boolean showDate) {
        this.taskList = taskList;
        this.listener = listener;
        this.showDate = showDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.task_item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.taskTitle.setText(currentTask.getTitle());
        holder.taskSubject.setText(currentTask.getSubject().getTitle());
        holder.taskSubjectBackground.setBackgroundClr(currentTask.getSubject().getColor());
        // subtask count
        if (currentTask.getSubtasks().isEmpty()) {
            holder.layoutSubtaskCount.setVisibility(View.GONE);
        } else {
            holder.layoutSubtaskCount.setVisibility(View.VISIBLE);
            String subtaskCount = context.getString(R.string.task_item_subtask_count,
                    currentTask.subtaskDoneCount(), currentTask.getSubtasks().size());
            holder.textSubtaskCount.setText(subtaskCount);
        }
        if (showDate) {
            holder.taskDate.setText(Util.getDateString(currentTask.getDate(), context));
        } else {
            if (currentTask.hasAlarm()) {
                holder.taskDate.setText(Util.getTimeString(currentTask.getDate()));
            }
        }
        // if hasn't flag, the flag color doesn't appear
        if (currentTask.getFlagColor().equals(Util.FlagColors.NONE)) {
            holder.taskFlag.setVisibility(View.INVISIBLE);
        } else { // set the color saved for the flag
            holder.taskFlag.setVisibility(View.VISIBLE);
            holder.taskFlag.getBackground().setTint(Color.parseColor(currentTask.getFlagColor()));
        }
        if (!currentTask.getNote().isEmpty()) {
            holder.taskNote.setVisibility(View.VISIBLE);
            holder.taskNote.setText(currentTask.getNote());
        }


        // configuring first and last item for correct shadow
        if (position == 0)
            holder.shadowView.setShadowMarginTop(30);
        if (position == getItemCount() - 1)
            holder.shadowView.setShadowMarginBottom(30);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnTaskDone;
        TextView taskTitle;
        TextView taskDate;
        TextView taskNote;
        View taskFlag;
        LinearLayout layoutSubtaskCount;
        TextView textSubtaskCount;
        ShadowView shadowView;
        TextView taskSubject;
        ShadowView taskSubjectBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTaskDone = itemView.findViewById(R.id.btn_checkbox_task);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskNote = itemView.findViewById(R.id.taskNote);
            taskFlag = itemView.findViewById(R.id.task_flag_item);
            layoutSubtaskCount = itemView.findViewById(R.id.layoutSubtaskCount);
            textSubtaskCount = itemView.findViewById(R.id.textSubtaskCount);
            shadowView = itemView.findViewById(R.id.shadowView);
            taskSubject = itemView.findViewById(R.id.task_subject);
            taskSubjectBackground = itemView.findViewById(R.id.task_subject_background);

            if (listener != null) {
                btnTaskDone.setOnClickListener(v -> listener.onTaskChecked(taskList.get(getAdapterPosition())));
                itemView.setOnClickListener(v -> listener.onItemClick(taskList.get(getAdapterPosition())));
            }
        }
    }

    public void deleteTask(int position) {
        Task task = taskList.get(position);
        listener.onDeleteTask(task);
    }
}
