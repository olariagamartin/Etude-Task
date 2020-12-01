package com.themarto.etudetask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.models.Subject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<Subject> subjectList;

    private Util.MyListener mListener;

    private Context context;

    private int selectedSubject;

    public SubjectAdapter(List<Subject> subjectList, int selectedSubject) {
        this.subjectList = subjectList;
        this.selectedSubject = selectedSubject;
    }

    public void setListener(Util.MyListener listener) {
        mListener = listener;
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
        int sections = currentSubject.getSectionList().size();
        String sectionCount = sections == 1 ? sections + " section" : sections + " sections";
        holder.subjectCountSections.setText(sectionCount);
        if (position == selectedSubject){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.itemBackground));
            holder.subjectTitle.setTextColor(context.getResources().getColor(R.color.blue_button));
        }
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTitle;
        TextView subjectCountSections;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            subjectTitle = itemView.findViewById(R.id.subjectTitle);
            cardView = (CardView) itemView.getRootView();
            subjectCountSections = itemView.findViewById(R.id.subjectCountSections);
            if(mListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

}
