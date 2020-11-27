package com.themarto.etudetask.fragments.bottomsheets;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.WorkManagerAlarm;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;

public class BottomSheetAddTask extends BottomSheetDialogFragment {

    private BottomSheetAddTaskBinding binding;
    private SharedViewModel viewModel;
    private Calendar actual;
    private Calendar calendar = Calendar.getInstance();

    public BottomSheetAddTask() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddTaskBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViewsBehavior();
    }

    private void setViewsBehavior() {
        setEditTextTitleBehavior();
        setBtnAddDetailsBehavior();
        setBtnAddDateBehavior();
        setBtnAddTimeBehavior();
        setBtnSaveTaskBehavior();
        setChipsBehavior();

        disableImageButton(binding.btnAddTaskTime);
    }

    // Behavior methods
    private void setEditTextTitleBehavior() {
        binding.editTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    disableTextButton(binding.btnSaveTask);
                } else {
                    enableTextButton(binding.btnSaveTask);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setBtnAddDetailsBehavior() {
        binding.btnAddTaskDetails.setOnClickListener(v -> {
            binding.editTextNewTaskDetails.setVisibility(View.VISIBLE);
            binding.editTextNewTaskDetails.requestFocus();
        });
    }

    private void setBtnAddDateBehavior() {
        binding.btnAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());
    }

    private void setBtnAddTimeBehavior() {
        binding.btnAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void setBtnSaveTaskBehavior() {
        binding.btnSaveTask.setOnClickListener(v -> {
            Task task = getTask();
            viewModel.addTask(task);
            dismiss();
        });
    }

    private void setChipsBehavior() {
        binding.chipAddTaskDueDate.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.layoutChips);
            binding.chipAddTaskDueDate.setVisibility(View.GONE);
            binding.btnAddTaskDueDate.setVisibility(View.VISIBLE);
            binding.chipAddTaskTime.performCloseIconClick();
            disableImageButton(binding.btnAddTaskTime);
        });

        binding.chipAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());

        binding.chipAddTaskTime.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.layoutChips);
            binding.chipAddTaskTime.setVisibility(View.GONE);
            binding.btnAddTaskTime.setVisibility(View.VISIBLE);
        });

        binding.chipAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }
    //...

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

                        TransitionManager.beginDelayedTransition(binding.layoutChips);
                        binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
                        binding.btnAddTaskDueDate.setVisibility(View.GONE);

                        binding.chipAddTaskDueDate.setText(Util.getDateString(calendar.getTime()));

                        enableImageButton(binding.btnAddTaskTime);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void lunchTimePicker() {
        actual = Calendar.getInstance();
        int hour = actual.get(Calendar.HOUR_OF_DAY);
        int min = actual.get(Calendar.MINUTE);

        // todo: change theme
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.ThemeOverlay_App_MaterialAlertDialog,
                (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    TransitionManager.beginDelayedTransition(binding.layoutChips);
                    binding.chipAddTaskTime.setVisibility(View.VISIBLE);
                    binding.btnAddTaskTime.setVisibility(View.GONE);

                    binding.chipAddTaskTime
                            .setText(Util.getTimeString(calendar.getTime()));

                }, hour, min, false);
        timePickerDialog.show();
    }

    private Task getTask() {
        String title = binding.editTextNewTask.getText().toString();
        String details = "";
        if (binding.editTextNewTaskDetails.getVisibility() == View.VISIBLE
                && !binding.editTextNewTaskDetails.getText().toString().isEmpty()) {
            details = binding.editTextNewTaskDetails.getText().toString();
        }
        Task nTask = new Task(title, details);
        if (binding.chipAddTaskDueDate.getVisibility() == View.VISIBLE) {
            if (binding.chipAddTaskTime.getVisibility() == View.VISIBLE) {
                saveAlarm(nTask);
            }
            nTask.setDate(calendar.getTime());
        }
        return nTask;
    }

    // todo: maybe util methods
    private void disableTextButton(Button btn) {
        btn.setEnabled(false);
        btn.setTextColor(getResources()
                .getColor(R.color.green1));
    }

    private void enableTextButton(Button btn) {
        btn.setEnabled(true);
        btn.setTextColor(getResources()
                .getColor(R.color.blue_button));
    }

    private void disableImageButton(ImageButton btn) {
        btn.setEnabled(false);
        btn.setImageTintList(AppCompatResources.getColorStateList(getContext(),
                R.color.green1));
    }

    private void enableImageButton(AppCompatImageButton btn) {
        btn.setEnabled(true);
        btn.setImageTintList(AppCompatResources.getColorStateList(getContext(),
                R.color.blue_button));
    }

    private Data saveData(String title, String detail, String taskId, String sectionId) {
        return new Data.Builder()
                .putString("title", title)
                .putString("detail", detail)
                .putString("task_id", taskId)
                .putString("section_id", sectionId)
                .build();
    }
    //...

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
}
