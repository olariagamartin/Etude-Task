package com.themarto.etudetask.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Subject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private final List<Subject> subjectList;

    private final SubjectListener listener;

    private Context context;

    public SubjectAdapter(List<Subject> subjectList, SubjectListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject currentSubject = subjectList.get(position);
        holder.subjectTitle.setText(currentSubject.getTitle());
        String taskCount = context.getString(R.string.subject_item_task_count,
                currentSubject.getDoneSize(), currentSubject.getTaskList().size());
        holder.subjectCountSections.setText(taskCount);
        holder.subjectColor.getBackground().setTint(currentSubject.getColor());
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView subjectTitle;
        TextView subjectCountSections;
        CardView cardView;
        View subjectColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTitle = itemView.findViewById(R.id.subjectTitle);
            cardView = (CardView) itemView.getRootView();
            subjectCountSections = itemView.findViewById(R.id.subjectCountSections);
            subjectColor = itemView.findViewById(R.id.subject_color_item);
            if(listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(subjectList.get(getAdapterPosition())));
            }

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(subjectList.get(getAdapterPosition()).getTitle());
            MenuItem editSubject = menu.add(R.string.context_menu_subject_edit);
            MenuItem deleteSubject = menu.add(R.string.context_menu_subject_delete);

            editSubject.setOnMenuItemClickListener(item -> {
                listener.onEditSubjectClick(subjectList.get(getAdapterPosition()));
                return true;
            });

            deleteSubject.setOnMenuItemClickListener(item -> {
                listener.onDeleteSubjectClick(subjectList.get(getAdapterPosition()));
                return true;
            });
        }
    }

    public interface SubjectListener {
        void onItemClick(Subject subject);
        void onEditSubjectClick(Subject subject);
        void onDeleteSubjectClick(Subject subject);
    }

}
