package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;

    private Util.MyListener mListener;

    private TaskListener taskListener;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setListener(Util.MyListener listener){
        mListener = listener;
    }

    public void setTaskListener (TaskListener taskListener) {
        this.taskListener = taskListener;
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
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton btnTaskDone;
        TextView taskTitle;
        TextView taskDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            btnTaskDone = itemView.findViewById(R.id.btn_checkbox_task);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });

            btnTaskDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskListener.onTaskChecked(getAdapterPosition());
                }
            });
        }
    }

    public interface TaskListener {
        public void onTaskChecked (int position);
    }
}
