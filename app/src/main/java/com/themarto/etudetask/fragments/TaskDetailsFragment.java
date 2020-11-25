package com.themarto.etudetask.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.android.material.button.MaterialButton;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.databinding.FragmentTaskDetailsBinding;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

public class TaskDetailsFragment extends Fragment {

    private SharedViewModel viewModel;
    private FragmentTaskDetailsBinding binding;
    private Task currentTask;
    private boolean deleted = false;
    private Calendar actual = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();

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
        btnAddDateBehavior();
        btnAddTimeBehavior();
        chipsBehavior();
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

    private void btnAddDateBehavior(){
        binding.btnDueDateTaskDetails.setOnClickListener(v -> lunchDatePicker());
    }

    private void btnAddTimeBehavior(){
        binding.btnNotificationTaskDetails.setOnClickListener(v -> lunchTimePicker());
    }

    private void chipsBehavior() {
        binding.chipDueDateTaskDetails.setOnCloseIconClickListener(v -> {
            switchVisibility(binding.chipDueDateTaskDetails,
                    binding.btnDueDateTaskDetails, binding.layoutChipsTaskDetails);
        });

        binding.chipDueDateTaskDetails.setOnClickListener(v -> lunchDatePicker());

        binding.chipNotificationTaskDetails.setOnCloseIconClickListener(v -> {
            switchVisibility(binding.chipNotificationTaskDetails,
                    binding.btnNotificationTaskDetails, binding.layoutChipsTaskDetails);
        });

        binding.chipNotificationTaskDetails.setOnClickListener(v -> lunchTimePicker());
    }

    private void taskDescriptionBehavior() {
        String details = currentTask.getDetails();
        binding.editTextTaskDescription
                .setText(details);
    }

    private void lunchDatePicker(){
        actual = Calendar.getInstance();
        int year = actual.get(Calendar.YEAR);
        int month = actual.get(Calendar.MONTH);
        int day = actual.get(Calendar.DAY_OF_MONTH);

        // todo: change theme
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view12, int year, int month, int dayOfMonth) {
                        // save the value we pick
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);

                        // todo: extract method
                        TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails);
                        binding.chipDueDateTaskDetails.setVisibility(View.VISIBLE);
                        binding.btnDueDateTaskDetails.setVisibility(View.GONE);
                        binding.chipDueDateTaskDetails.setText(Util.getDateString(calendar.getTime()));

                        enableImageButton(binding.btnNotificationTaskDetails);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void lunchTimePicker(){
        actual = Calendar.getInstance();
        int hour = actual.get(Calendar.HOUR_OF_DAY);
        int min = actual.get(Calendar.MINUTE);

        // todo: change theme
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog,
                (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    TransitionManager.beginDelayedTransition(binding.layoutChipsTaskDetails);
                    binding.chipNotificationTaskDetails.setVisibility(View.VISIBLE);
                    binding.btnNotificationTaskDetails.setVisibility(View.GONE);

                    binding.chipNotificationTaskDetails
                            .setText(Util.getTimeString(calendar.getTime()));

                }, hour, min, false);
        timePickerDialog.show();
    }

    private void switchVisibility (View visible, View notVisible, View viewGroup){
        TransitionManager.beginDelayedTransition((ViewGroup) viewGroup, new AutoTransition());
        visible.setVisibility(View.GONE);
        notVisible.setVisibility(View.VISIBLE);
    }

    private void enableImageButton(MaterialButton btn){
        btn.setEnabled(true);
        btn.setIconTint(AppCompatResources.getColorStateList(getContext(),
                R.color.blue_button));
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