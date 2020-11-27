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
import com.themarto.etudetask.WorkManagerAlarm;
import com.themarto.etudetask.databinding.FragmentTaskDetailsBinding;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.WorkManager;

public class TaskDetailsFragment extends Fragment {

    // maybe i could try to save changes and in onPause method save them all

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
                loadContent();
                setViewBehavior();
            }
        });
    }

    private void loadContent() {
        disableButton(binding.btnNotificationTaskDetails);
        topTitle();
        binding.editTextTaskTitle.setText(currentTask.getTitle());
        taskDescription();
        if (currentTask.getDate() != null) {
            binding.chipDueDateTaskDetails.setVisibility(View.VISIBLE);
            binding.btnDueDateTaskDetails.setVisibility(View.GONE);
            binding.chipDueDateTaskDetails.setText(Util.getDateString(currentTask.getDate()));
            enableButton(binding.btnNotificationTaskDetails);
            calendar.setTime(currentTask.getDate());
            if (currentTask.hasAlarm()) {
                binding.chipNotificationTaskDetails.setVisibility(View.VISIBLE);
                binding.btnNotificationTaskDetails.setVisibility(View.GONE);
                binding.chipNotificationTaskDetails
                        .setText(Util.getTimeString(currentTask.getDate()));
            }
        }
    }

    // maybe change and send task
    private void setViewBehavior() {
        backButtonBehavior();
        deleteButtonBehavior();
        taskTitleBehavior();
        btnAddDateBehavior();
        btnAddTimeBehavior();
        chipsBehavior();
    }

    private void backButtonBehavior() {
        binding.btnArrowBackTaskDetails.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void topTitle() {
        String title = viewModel.getSelectedSection().getValue().getTitle();
        binding.topTitleTaskDetails.setText(title);
    }

    private void deleteButtonBehavior() {
        binding.btnDeleteTaskDetails.setOnClickListener(v -> {
            viewModel.deleteTask();
            deleted = true;
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void taskTitleBehavior() {
        binding.editTextTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void btnAddDateBehavior() {
        binding.btnDueDateTaskDetails.setOnClickListener(v -> lunchDatePicker());
    }

    private void btnAddTimeBehavior() {
        binding.btnNotificationTaskDetails.setOnClickListener(v -> lunchTimePicker());
    }

    private void chipsBehavior() {
        binding.chipDueDateTaskDetails.setOnCloseIconClickListener(v -> {
            binding.chipNotificationTaskDetails.performCloseIconClick();
            disableButton(binding.btnNotificationTaskDetails);
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

    private void taskDescription() {
        String details = currentTask.getDetails();
        binding.editTextTaskDescription
                .setText(details);
    }

    private void lunchDatePicker() {
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

                        enableButton(binding.btnNotificationTaskDetails);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void lunchTimePicker() {
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

    // refactor method
    private void switchVisibility(View visible, View notVisible, View viewGroup) {
        TransitionManager.beginDelayedTransition((ViewGroup) viewGroup, new AutoTransition());
        visible.setVisibility(View.GONE);
        notVisible.setVisibility(View.VISIBLE);
    }

    private void enableButton(MaterialButton btn) {
        btn.setEnabled(true);
        btn.setIconTint(AppCompatResources.getColorStateList(getContext(),
                R.color.blue_button));
    }

    private void disableButton(MaterialButton btn) {
        btn.setEnabled(false);
        btn.setIconTint(AppCompatResources.getColorStateList(getContext(),
                R.color.green3));
    }

    private void commitChanges() {
        Task updatedTask = new Task(getTitle());
        updatedTask.setDetails(binding.editTextTaskDescription.getText().toString());
        updatedTask.setId(currentTask.getId());

        if (currentTask.hasAlarm()) {
            WorkManager.getInstance(getContext()).cancelWorkById(UUID
                    .fromString(currentTask.getAlarmStringId()));
        }
        // i need to find a better way (really!)
        if (binding.chipDueDateTaskDetails.getVisibility() == View.VISIBLE) {
            updatedTask.setDate(calendar.getTime());
            if(binding.chipNotificationTaskDetails.getVisibility() == View.VISIBLE) {
                saveAlarm(updatedTask);
            }
        }
        viewModel.updateTask(updatedTask);
    }

    private String getTitle() {
        String taskTitle = binding.editTextTaskTitle.getText().toString();
        if (taskTitle.isEmpty()) {
            taskTitle = currentTask.getTitle();
        }
        return taskTitle;
    }

    private void saveAlarm(Task task) {
        long alertTime = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (alertTime > 0) {
            String notificationTitle = task.getTitle();
            Subject subject = viewModel.getSelectedSubject().getValue();
            Section section = viewModel.getSelectedSection().getValue();
            String notificationDetail = subject.getTitle() + " - " + section.getTitle();
            Data data = saveData(notificationTitle, notificationDetail, task.getId(), section.getId());

            String alarmStringId = WorkManagerAlarm
                    .saveAlarm(alertTime, data, section.getId(), subject.getId());

            task.setAlarmStringId(alarmStringId);
        }
    }

    private Data saveData(String title, String detail, String taskId, String sectionId) {
        return new Data.Builder()
                .putString("title", title)
                .putString("detail", detail)
                .putString("task_id", taskId)
                .putString("section_id", sectionId)
                .build();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!deleted) {
            commitChanges();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}