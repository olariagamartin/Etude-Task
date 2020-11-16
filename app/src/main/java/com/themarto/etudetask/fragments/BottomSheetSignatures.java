package com.themarto.etudetask.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private SharedPreferences sharedPref;

    public BottomSheetSignatures() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSignaturesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        // TODO: take it in another method
        // prevent the key board cover buttons when add signature
        int minHeight = getResources().getDisplayMetrics().heightPixels / 2;
        binding.recyclerViewSignatures.setMinimumHeight(minHeight);

        recyclerViewSignatures = binding.recyclerViewSignatures;

        binding.addSignature.setOnClickListener(v -> runAddSignature());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getAllSignatures().observe(this, signatureList -> loadSignatures(signatureList));
    }

    // ACTIONS
    private void runAddSignature(){
        showAddElements();

        binding.editTextNewSignature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                if(title.isEmpty()){
                    disableButton(binding.btnSaveSignature);
                } else {
                    enableButton(binding.btnSaveSignature);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSaveSignature.setOnClickListener(v -> {
            String signatureTitle = binding.editTextNewSignature.getText().toString();
            saveSignature(signatureTitle);
            sharedPref.edit()
                    .putInt("SELECTED_SIGNATURE", viewModel.getAllSignatures().getValue().size() - 1 )
                    .apply();
            dismiss();
        });

        binding.editTextNewSignature.requestFocus();
    }

    private void enableButton(Button btn){
        binding.btnSaveSignature.setEnabled(true);
        binding.btnSaveSignature.setTextColor(getResources()
                .getColor(R.color.blue_button));
    }

    private void disableButton(Button btn){
        btn.setEnabled(false);
        btn.setTextColor(getResources()
                .getColor(R.color.green1));
    }

    private void showAddElements() {
        TransitionManager.beginDelayedTransition(binding.getRoot(), new AutoTransition());
        binding.parentViewTitle.setVisibility(View.GONE);
        binding.parentViewAddSignature.setVisibility(View.VISIBLE);
    }

    private void loadSignatures(List<Signature> signatureList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        int selectedSignature = sharedPref.getInt("SELECTED_SIGNATURE", 0);
        SignatureAdapter signatureAdapter = new SignatureAdapter(signatureList, selectedSignature);
        signatureAdapter.setListener(new Util.MyListener() {
            @Override
            public void onItemClick(int position) {
                SharedPreferences.Editor editor = sharedPref.edit();
                // todo: extract string
                editor.putInt("SELECTED_SIGNATURE", position);
                editor.apply();
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
