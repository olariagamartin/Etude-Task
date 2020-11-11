package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.themarto.etudetask.databinding.FragmentTaskDetailsBinding;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

public class TaskDetailsFragment extends Fragment {

    private SharedViewModel viewModel;

    private FragmentTaskDetailsBinding binding;

    private Task currentTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedTask().observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                currentTask = task;
                setViewBehavior();
            }
        });
    }

    private void setViewBehavior(){
        backButtonBehavior();
        topTitleBehavior();
        deleteButtonBehavior();
        taskTitleBehavior();
        layoutChipsBehavior();
        taskDescriptionBehavior();
    }

    private void backButtonBehavior(){
        binding.btnArrowBackTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save data
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    // TODO: set title
    private void topTitleBehavior(){
        String title = viewModel.getSelectedChapter().getValue().getTitle();
        binding.topTitleTaskDetails.setText(title);
    }

    private void deleteButtonBehavior(){
        binding.btnDeleteTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: asks, delete
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    private void taskTitleBehavior(){
        binding.editTextTaskTitle.setText(currentTask.getTitle());
        binding.editTextTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: deny when text is empty
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void layoutChipsBehavior() {
        binding.btnDueDateTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails, new AutoTransition());
                binding.btnDueDateTaskDetails.setVisibility(View.GONE);
                binding.chipDueDateTaskDetails.setVisibility(View.VISIBLE);
            }
        });

        binding.btnNotificationTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails, new AutoTransition());
                binding.btnNotificationTaskDetails.setVisibility(View.GONE);
                binding.chipNotificationTaskDetails.setVisibility(View.VISIBLE);
            }
        });

        binding.chipDueDateTaskDetails.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails, new AutoTransition());
                binding.btnDueDateTaskDetails.setVisibility(View.VISIBLE);
                binding.chipDueDateTaskDetails.setVisibility(View.GONE);
            }
        });

        binding.chipNotificationTaskDetails.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails, new AutoTransition());
                binding.btnNotificationTaskDetails.setVisibility(View.VISIBLE);
                binding.chipNotificationTaskDetails.setVisibility(View.GONE);
            }
        });
    }

    private void taskDescriptionBehavior() {
        binding.editTextTaskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: save
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}