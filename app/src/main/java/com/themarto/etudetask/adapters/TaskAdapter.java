package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.fragments.TasksFragmentDirections;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;

    private Util.MyListener mListener;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setListener(Util.MyListener listener){
        mListener = listener;
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
        holder.taskDone.setChecked(currentTask.isDone());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCheckBox taskDone;
        TextView taskTitle;
        TextView taskDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            taskDone = itemView.findViewById(R.id.switchTaskDone);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
