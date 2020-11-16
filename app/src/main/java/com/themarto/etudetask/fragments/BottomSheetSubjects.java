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
import com.themarto.etudetask.adapters.SubjectAdapter;
import com.themarto.etudetask.databinding.BottomSheetSubjectsBinding;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetSubjects extends BottomSheetDialogFragment {

    RecyclerView recyclerViewSubjects;

    private BottomSheetSubjectsBinding binding;

    private SharedViewModel viewModel;

    private SharedPreferences sharedPref;

    public BottomSheetSubjects() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSubjectsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        // TODO: take it in another method
        // prevent the key board cover buttons when add subject
        int minHeight = getResources().getDisplayMetrics().heightPixels / 2;
        binding.recyclerViewSubjects.setMinimumHeight(minHeight);

        recyclerViewSubjects = binding.recyclerViewSubjects;

        binding.addSubject.setOnClickListener(v -> runAddSubject());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getAllSubjects().observe(this, subjectList -> loadSubjects(subjectList));
    }

    // ACTIONS
    private void runAddSubject(){
        showAddElements();

        binding.editTextNewSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                if(title.isEmpty()){
                    disableButton(binding.btnSaveSubject);
                } else {
                    enableButton(binding.btnSaveSubject);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSaveSubject.setOnClickListener(v -> {
            String subjectTitle = binding.editTextNewSubject.getText().toString();
            saveSubject(subjectTitle);
            sharedPref.edit()
                    .putInt("SELECTED_SIGNATURE", viewModel.getAllSubjects().getValue().size() - 1 )
                    .apply();
            dismiss();
        });

        binding.editTextNewSubject.requestFocus();
    }

    private void enableButton(Button btn){
        binding.btnSaveSubject.setEnabled(true);
        binding.btnSaveSubject.setTextColor(getResources()
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
        binding.parentViewAddSubject.setVisibility(View.VISIBLE);
    }

    private void loadSubjects(List<Subject> subjectList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        int selectedSubject = sharedPref.getInt("SELECTED_SUBJECT", 0);
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjectList, selectedSubject);
        subjectAdapter.setListener(new Util.MyListener() {
            @Override
            public void onItemClick(int position) {
                SharedPreferences.Editor editor = sharedPref.edit();
                // todo: extract string
                editor.putInt("SELECTED_SUBJECT", position);
                editor.apply();
                viewModel.selectSubject(position);
                dismiss();
            }
        });
        recyclerViewSubjects.setLayoutManager(layoutManager);
        recyclerViewSubjects.setAdapter(subjectAdapter);
        recyclerViewSubjects.setHasFixedSize(true);
    }

    // CRUD
    private void saveSubject(String title){
        Subject subject = new Subject(title);
        viewModel.addSubject(subject);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
