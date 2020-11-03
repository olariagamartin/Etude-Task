package com.themarto.etudetask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.adapters.SignatureAdapter;
import com.themarto.etudetask.models.Signature;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheet extends BottomSheetDialogFragment {

    RecyclerView recyclerViewSignatures;

    public BottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_signatures, container, false);

        recyclerViewSignatures = view.findViewById(R.id.recyclerViewSignatures);

        showSignatures(Util.getSignatureListEx());

        return view;
    }

    private void showSignatures(List<Signature> signatureList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        SignatureAdapter signatureAdapter = new SignatureAdapter(signatureList);
        recyclerViewSignatures.setLayoutManager(layoutManager);
        recyclerViewSignatures.setAdapter(signatureAdapter);
        recyclerViewSignatures.setHasFixedSize(true);
    }
}
