package com.themarto.etudetask.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.SignatureAdapter;
import com.themarto.etudetask.databinding.BottomSheetSignaturesBinding;
import com.themarto.etudetask.models.Signature;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetSignatures extends BottomSheetDialogFragment {

    RecyclerView recyclerViewSignatures;

    private BottomSheetSignaturesBinding binding;

    private Context context;

    public BottomSheetSignatures() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSignaturesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        context = getContext();

        // prevent the key board cover buttons when add signature
        int minHeight = getResources().getDisplayMetrics().heightPixels / 2;
        binding.recyclerViewSignatures.setMinimumHeight(minHeight);

        recyclerViewSignatures = binding.recyclerViewSignatures;

        binding.addSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAddSignature();
            }
        });

        showSignatures(Util.getSignatureListEx());

        return view;
    }

    private void runAddSignature(){
        TransitionManager.beginDelayedTransition(binding.getRoot(), new AutoTransition());
        binding.parentViewTitle.setVisibility(View.GONE);
        binding.parentViewAddSignature.setVisibility(View.VISIBLE);

        binding.editTextNewSignature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: extract in a method (util)
                String title = s.toString();
                if(title.isEmpty()){
                    binding.btnSaveSignature.setEnabled(false);
                    binding.btnSaveSignature.setTextColor(getResources()
                            .getColor(R.color.green1));
                } else {
                    binding.btnSaveSignature.setEnabled(true);
                    binding.btnSaveSignature.setTextColor(getResources()
                            .getColor(R.color.blue_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSaveSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signatureTitle = binding.editTextNewSignature.getText().toString();
                binding.parentViewTitle.setVisibility(View.VISIBLE);
                binding.parentViewAddSignature.setVisibility(View.GONE);
                dismiss();
                saveSignature(signatureTitle);
            }
        });
        // when appearing the keyboard the UI makes noise
        // showSoftKeyboard(binding.editTextNewSignature);
        binding.editTextNewSignature.requestFocus();
    }

    private void saveSignature(String title){
        Toast.makeText(context, title + "saved", Toast.LENGTH_SHORT).show();
    }

    private void hideSoftKeyboard (View view){
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showSignatures(List<Signature> signatureList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        SignatureAdapter signatureAdapter = new SignatureAdapter(signatureList);
        recyclerViewSignatures.setLayoutManager(layoutManager);
        recyclerViewSignatures.setAdapter(signatureAdapter);
        recyclerViewSignatures.setHasFixedSize(true);
    }

}
