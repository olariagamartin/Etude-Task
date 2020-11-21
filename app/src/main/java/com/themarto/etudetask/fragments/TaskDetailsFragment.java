package com.themarto.etudetask.fragments;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

public class TaskDetailsFragment extends Fragment {

    private SharedViewModel viewModel;
    private FragmentTaskDetailsBinding binding;
    private Task currentTask;
    private boolean deleted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
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

    // maybe change and send task
    private void setViewBehavior(){
        backButtonBehavior();
        topTitleBehavior();
        deleteButtonBehavior();
        taskTitleBehavior();
        layoutChipsBehavior();
        taskDescriptionBehavior();
    }

    private void backButtonBehavior(){
        binding.btnArrowBackTaskDetails.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void topTitleBehavior(){
        String title = viewModel.getSelectedSection().getValue().getTitle();
        binding.topTitleTaskDetails.setText(title);
    }

    private void deleteButtonBehavior(){
        binding.btnDeleteTaskDetails.setOnClickListener(v -> {
            viewModel.deleteTask();
            deleted = true;
            Navigation.findNavController(v).navigateUp();
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
        binding.btnDueDateTaskDetails.setOnClickListener(v -> {
            switchVisibility(binding.btnDueDateTaskDetails,
                    binding.chipDueDateTaskDetails, binding.layoutChipsTaskDetails);
        });

        binding.btnNotificationTaskDetails.setOnClickListener(v -> {
            switchVisibility(binding.btnNotificationTaskDetails,
                    binding.chipNotificationTaskDetails, binding.layoutChipsTaskDetails);
        });

        binding.chipDueDateTaskDetails.setOnCloseIconClickListener(v -> {
            switchVisibility(binding.chipDueDateTaskDetails,
                    binding.btnDueDateTaskDetails, binding.layoutChipsTaskDetails);
        });

        binding.chipNotificationTaskDetails.setOnCloseIconClickListener(v -> {
            switchVisibility(binding.chipNotificationTaskDetails,
                    binding.btnNotificationTaskDetails, binding.layoutChipsTaskDetails);
        });
    }

    private void taskDescriptionBehavior() {
        String details = currentTask.getDetails();
        binding.editTextTaskDescription
                .setText(details);
    }

    private void switchVisibility (View visible, View notVisible, View viewGroup){
        TransitionManager.beginDelayedTransition((ViewGroup) viewGroup, new AutoTransition());
        visible.setVisibility(View.GONE);
        notVisible.setVisibility(View.VISIBLE);
    }

    private void commitChanges (){
        validateAndSaveTitle();
        String taskDetails = binding.editTextTaskDescription.getText().toString();
        viewModel.updateTaskDetails(taskDetails);
    }

    private void validateAndSaveTitle () {
        String taskTitle = binding.editTextTaskTitle.getText().toString();
        if (!taskTitle.isEmpty()) {
            viewModel.updateTaskTitle(taskTitle);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!deleted){
            commitChanges();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}