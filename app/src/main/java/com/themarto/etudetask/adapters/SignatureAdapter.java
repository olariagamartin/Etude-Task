package com.themarto.etudetask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.models.Signature;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SignatureAdapter extends RecyclerView.Adapter<SignatureAdapter.ViewHolder> {

    private List<Signature> signatureList;

    public SignatureAdapter(List<Signature> signatureList) {
        this.signatureList = signatureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.signature_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Signature currentSignature = signatureList.get(position);
        holder.signatureTitle.setText(currentSignature.getTitle());
    }

    @Override
    public int getItemCount() {
        return signatureList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView signatureTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: do it with view binding
            signatureTitle = itemView.findViewById(R.id.signatureTitle);
        }
    }
}
