package com.themarto.etudetask.adapters;

import android.graphics.Paint;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskDoneAdapter extends RecyclerView.Adapter<TaskDoneAdapter.ViewHolder> {

    private List<Task> taskDoneList;

    private TaskDoneListener listener;

    public TaskDoneAdapter(List<Task> taskDoneList, TaskDoneListener listener) {
        this.taskDoneList = taskDoneList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_done, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task currentTask = taskDoneList.get(position);
        holder.taskTitle.setText(currentTask.getTitle());
    }

    @Override
    public int getItemCount() {
        return taskDoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnTaskDone;
        TextView taskTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTaskDone = itemView.findViewById(R.id.btn_task_done);
            taskTitle = itemView.findViewById(R.id.taskDoneTitle);
            taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if(listener != null) {
                btnTaskDone.setOnClickListener(v -> listener.onBtnDoneClick(getAdapterPosition()));
            }
        }
    }

    public interface TaskDoneListener {
        void onBtnDoneClick(int position);
    }
}
