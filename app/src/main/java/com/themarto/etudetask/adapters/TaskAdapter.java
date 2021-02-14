package com.themarto.etudetask.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopeer.shadow.ShadowView;
import com.themarto.etudetask.R;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> taskList;

    private final TaskListener listener;

    public TaskAdapter(List<Task> taskList, TaskListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.taskTitle.setText(currentTask.getTitle());
        holder.taskDate.setText(currentTask.getDateStr());
        // configuring first and last item for correct shadow
        if (position == 0)
            holder.shadowView.setShadowMarginTop(30);
        if (position == getItemCount() - 1)
            holder.shadowView.setShadowMarginBottom(30);
        if (currentTask.hasAlarm()) {
            holder.notificationIcon.setVisibility(View.VISIBLE);
        } else {
            holder.notificationIcon.setVisibility(View.INVISIBLE);
        }
        // if hasn't flag, the flag color doesn't appear
        if (currentTask.getFlagColor().equals(Util.FlagColors.NONE)) {
            holder.taskFlag.setVisibility(View.INVISIBLE);
        } else { // set the color saved for the flag
            holder.taskFlag.setVisibility(View.VISIBLE);
            holder.taskFlag.getBackground().setTint(Color.parseColor(currentTask.getFlagColor()));
        }
        if (currentTask.getSubtasks().isEmpty()) {
            holder.layoutSubtaskCount.setVisibility(View.GONE);
        } else {
            holder.layoutSubtaskCount.setVisibility(View.VISIBLE);
            String subtaskCount = "" + currentTask.subtaskDoneCount() + " / " + currentTask.getSubtasks().size();
            holder.textSubtaskCount.setText(subtaskCount);
        }
        if (!currentTask.getNote().isEmpty()) {
            holder.taskNote.setVisibility(View.VISIBLE);
            holder.taskNote.setText(currentTask.getNote());
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton btnTaskDone;
        TextView taskTitle;
        TextView taskDate;
        TextView taskNote;
        View taskFlag;
        LinearLayout layoutSubtaskCount;
        TextView textSubtaskCount;
        AppCompatImageView notificationIcon;
        ShadowView shadowView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTaskDone = itemView.findViewById(R.id.btn_checkbox_task);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskNote = itemView.findViewById(R.id.taskNote);
            taskFlag = itemView.findViewById(R.id.task_flag_item);
            layoutSubtaskCount = itemView.findViewById(R.id.layoutSubtaskCount);
            textSubtaskCount = itemView.findViewById(R.id.textSubtaskCount);
            notificationIcon = itemView.findViewById(R.id.notification_icon);
            shadowView = itemView.findViewById(R.id.shadowView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(taskList.get(getAdapterPosition()));
                }
            });

            btnTaskDone.setOnClickListener(v -> listener.onTaskChecked(taskList.get(getAdapterPosition())));
        }
    }

    public void deleteTask(int position) {
        Task task = taskList.get(position);
        listener.onDeleteTask(task);
    }

    public interface TaskListener {
        void onItemClick(Task task);
        void onTaskChecked(Task task);
        void onDeleteTask(Task task);
    }
}
