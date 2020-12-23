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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.themarto.etudetask.R;
import com.themarto.etudetask.utils.Util;
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

    private SharedViewModel viewModel;
    private FragmentTaskDetailsBinding binding;
    private Task currentTask;
    /* because the updates are made in onPause() method we need to know
    if the delete button has been clicked */
    private boolean deleted = false;
    /* one calendar is used to store date and time for the notification
    and other is used to get the actual date and time */
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

    private void setViewBehavior() {
        backButtonBehavior();
        deleteButtonBehavior();
        btnAddDateBehavior();
        btnAddTimeBehavior();
        chipsBehavior();
    }

    private void backButtonBehavior() {
        binding.btnArrowBackTaskDetails.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void topTitle() {
        String title = viewModel.getSelectedSection().getValue().getTitle();
        binding.topTitleTaskDetails.setText(title);
    }

    private void deleteButtonBehavior() {
        binding.btnDeleteTaskDetails.setOnClickListener(v -> showDialogDeleteTask());
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view12, int year, int month, int dayOfMonth) {
                        // save the value we pick
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);

                        notifyDateSet();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void notifyDateSet(){
        switchVisibility(binding.btnDueDateTaskDetails, binding.chipDueDateTaskDetails,
                binding.layoutChipsTaskDetails);

        binding.chipDueDateTaskDetails.setText(Util.getDateString(calendar.getTime()));

        enableButton(binding.btnNotificationTaskDetails);
    }

    private void lunchTimePicker() {
        actual = Calendar.getInstance();
        int hour = actual.get(Calendar.HOUR_OF_DAY);
        int min = actual.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    notifyTimeSet();

                }, hour, min, false);
        timePickerDialog.show();
    }

    private void notifyTimeSet(){
        switchVisibility(binding.btnNotificationTaskDetails, binding.chipNotificationTaskDetails,
                binding.layoutChipsTaskDetails);

        binding.chipNotificationTaskDetails
                .setText(Util.getTimeString(calendar.getTime()));
    }

    /**
     * Change the visibility of passed views
     * @param visible the view is visible and will be gone
     * @param gone the view is gone and will be visible
     * @param viewGroup the content group of the views
     */
    private void switchVisibility(View visible, View gone, View viewGroup) {
        TransitionManager.beginDelayedTransition((ViewGroup) viewGroup, new AutoTransition());
        visible.setVisibility(View.GONE);
        gone.setVisibility(View.VISIBLE);
    }

    private void enableButton(MaterialButton btn) {
        btn.setEnabled(true);
        btn.setIconTint(AppCompatResources.getColorStateList(requireContext(),
                R.color.blue_grey_dark));
    }

    private void disableButton(MaterialButton btn) {
        btn.setEnabled(false);
        btn.setIconTint(AppCompatResources.getColorStateList(requireContext(),
                R.color.green3));
    }

    private void commitChanges() {
        Task updatedTask = new Task(getTitle());
        updatedTask.setDetails(binding.editTextTaskDescription.getText().toString());
        updatedTask.setId(currentTask.getId());

        deleteAlarm(currentTask);

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

    private void deleteAlarm(Task task) {
        if(task.hasAlarm()) {
            WorkManager.getInstance(requireContext()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
        }
    }

    private void saveAlarm(Task task) {
        long alertTime = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (alertTime > 0) {
            String notificationTitle = task.getTitle();
            Subject subject = viewModel.getSelectedSubject().getValue();
            Section section = viewModel.getSelectedSection().getValue();
            String notificationDetail = subject.getTitle() + " - " + section.getTitle();
            Data data = Util.saveNotificationData(notificationTitle, notificationDetail, task.getId(), section.getId());

            String alarmStringId = WorkManagerAlarm
                    .saveAlarm(alertTime, data, section.getId(), subject.getId(), requireContext());

            task.setAlarmStringId(alarmStringId);
        }
    }

    public void showDialogDeleteTask() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("The task will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteTask();
                    deleted = true;
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                }).show();
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