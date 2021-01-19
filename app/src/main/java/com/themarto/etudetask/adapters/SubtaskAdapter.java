package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Subtask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.ViewHolder> {

    private List<Subtask> subtaskList;

    public SubtaskAdapter(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtask_item, parent, false);
        return new SubtaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subtask subtask  = subtaskList.get(position);
        holder.editTextTitle.setText(subtask.getTitle());
        if (subtask.isDone()){
            holder.doneBtn.setImageResource(R.drawable.ic_done);
        }
    }

    @Override
    public int getItemCount() {
        return subtaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageButton doneBtn;
        EditText editTextTitle;
        AppCompatImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doneBtn = itemView.findViewById(R.id.btn_checkbox_subtask);
            editTextTitle = itemView.findViewById(R.id.subtask_title);
            deleteBtn = itemView.findViewById(R.id.btn_delete_subtask);
        }
    }
}
