package com.themarto.etudetask.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Subtask;
import com.themarto.etudetask.utils.MyTextWatcher;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.ViewHolder> {

    private List<Subtask> subtaskList;
    private SubtaskListener listener;

    public SubtaskAdapter(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void setListener(SubtaskListener listener){
        this.listener = listener;
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
            // todo: set text color
        }
        holder.bind(position);
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

        public void bind(int position) {
            if (listener != null){
                doneBtn.setOnClickListener(v -> listener.onDoneSubtask(position));
                editTextTitle.addTextChangedListener(listener.afterEditTitle(position));
                deleteBtn.setOnClickListener(v -> listener.onDeleteSubtask(position));
            }
        }
    }

    public interface SubtaskListener {
        void onDoneSubtask(int position);
        void onDeleteSubtask(int position);
        TextWatcher afterEditTitle(int position);
    }
}
