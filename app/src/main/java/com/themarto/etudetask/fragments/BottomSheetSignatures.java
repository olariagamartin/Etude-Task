package com.themarto.etudetask.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.SignatureAdapter;
import com.themarto.etudetask.databinding.BottomSheetSignaturesBinding;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetSignatures extends BottomSheetDialogFragment {

    RecyclerView recyclerViewSignatures;

    private BottomSheetSignaturesBinding binding;

    private SharedViewModel viewModel;

    public BottomSheetSignatures() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSignaturesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // TODO: take it in another method
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getAllSignatures().observe(this, new Observer<List<Signature>>() {
            @Override
            public void onChanged(List<Signature> signatureList) {
                loadSignatures(signatureList);
            }
        });
    }

    // ACTIONS
    private void runAddSignature(){
        // TODO: showAddElements
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

    public void hideSoftKeyboard (View view){
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void loadSignatures(List<Signature> signatureList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        SignatureAdapter signatureAdapter = new SignatureAdapter(signatureList);
        signatureAdapter.setListener(new Util.MyListener() {
            @Override
            public void onItemClick(int position) {
                viewModel.selectSignature(position);
                dismiss();
            }
        });
        recyclerViewSignatures.setLayoutManager(layoutManager);
        recyclerViewSignatures.setAdapter(signatureAdapter);
        recyclerViewSignatures.setHasFixedSize(true);
    }

    // CRUD
    private void saveSignature(String title){
        Signature signature = new Signature(title);
        viewModel.addSignature(signature);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
