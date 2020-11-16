package com.themarto.etudetask.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SignatureAdapter extends RecyclerView.Adapter<SignatureAdapter.ViewHolder> {

    private List<Signature> signatureList;

    private Util.MyListener mListener;

    private Context context;

    private int selectedSignature;

    public SignatureAdapter(List<Signature> signatureList, int selectedSignature) {
        this.signatureList = signatureList;
        this.selectedSignature = selectedSignature;
    }

    public void setListener(Util.MyListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.signature_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Signature currentSignature = signatureList.get(position);
        holder.signatureTitle.setText(currentSignature.getTitle());
        // TODO: set color if it's the current signature
        if (position == selectedSignature){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.itemBackground));
            holder.signatureTitle.setTextColor(context.getResources().getColor(R.color.blue_button));
        }
    }

    @Override
    public int getItemCount() {
        return signatureList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView signatureTitle;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            signatureTitle = itemView.findViewById(R.id.signatureTitle);
            cardView = (CardView) itemView.getRootView();
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
