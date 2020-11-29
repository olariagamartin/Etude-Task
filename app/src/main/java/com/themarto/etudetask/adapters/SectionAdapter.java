package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.models.Section;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private List<Section> sectionList;

    private Util.MyListener mListener;

    public SectionAdapter(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    // TODO: make it obligatory
    public void setListener(Util.MyListener listener){
        mListener = listener;
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
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            sectionTitle = itemView.findViewById(R.id.textViewSectionTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
