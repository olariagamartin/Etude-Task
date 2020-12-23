package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.models.Section;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private List<Section> sectionList;

    private SectionListener listener;

    public SectionAdapter(List<Section> sectionList, SectionListener listener) {
        this.sectionList = sectionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Section currentSection = sectionList.get(position);
        holder.sectionTitle.setText(currentSection.getTitle());
        String tasksCount = currentSection.getTaskDoneList().size() + " of " +
                (currentSection.getTaskDoneList().size() + currentSection.getTaskList().size());
        holder.tasksCount.setText(tasksCount);

        if (currentSection.getTaskDoneList().size() != 0 &&
                currentSection.getTaskList().size() == 0) {
            holder.imageSectionDone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        TextView tasksCount;
        ImageView imageSectionDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.textViewSectionTitle);
            tasksCount = itemView.findViewById(R.id.textViewTasksCount);
            imageSectionDone = itemView.findViewById(R.id.imageSectionDone);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface SectionListener {
        void onItemClick(int position);
        // Todo: add delete and edit
    }
}
