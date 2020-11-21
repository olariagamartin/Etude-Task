package com.themarto.etudetask.fragments.bottomsheets;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.R;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class BottomSheetAddTask extends BottomSheetDialogFragment {

    private BottomSheetAddTaskBinding binding;
    private SharedViewModel viewModel;
    private Calendar actual = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();

    public BottomSheetAddTask() { }

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

    private void setViewsBehavior(){
        setEditTextTitleBehavior();
        setBtnAddDetailsBehavior();
        setBtnAddDateBehavior();
        setBtnAddTimeBehavior();
        setBtnSaveTaskBehavior();
    }

    // Behavior methods
    private void setEditTextTitleBehavior(){
        binding.editTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    disableBtnSaveTask(binding.btnSaveTask);
                } else {
                    enableBtnSaveTask(binding.btnSaveTask);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setBtnAddDetailsBehavior(){
        binding.btnAddTaskDetails.setOnClickListener(v -> {
            binding.editTextNewTaskDetails.setVisibility(View.VISIBLE);
            binding.editTextNewTaskDetails.requestFocus();
        });
    }

    private void setBtnAddDateBehavior(){
        binding.btnAddTaskDueDate.setOnClickListener(v -> {
            int year = actual.get(Calendar.YEAR);
            int month = actual.get(Calendar.MONTH);
            int day = actual.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view12, int year, int month, int dayOfMonth) {
                            // save the value we pick
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.YEAR, year);

                            binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
                            binding.btnAddTaskDueDate.setVisibility(View.GONE);
                            binding.chipAddTaskDueDate.setText(dateToString(calendar.getTime()));
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setBtnAddTimeBehavior(){
        binding.btnAddTaskTime.setOnClickListener(v -> {
            int hour = actual.get(Calendar.HOUR_OF_DAY);
            int min = actual.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view1, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            binding.chipAddTaskTime.setVisibility(View.VISIBLE);
                            binding.btnAddTaskTime.setVisibility(View.GONE);
                            binding.chipAddTaskTime
                                    .setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, hour, min, false);
            timePickerDialog.show();
        });
    }

    private void setBtnSaveTaskBehavior(){
        binding.btnSaveTask.setOnClickListener(v -> {
            Task task = getTask();
            viewModel.addTask(task);
            dismiss();
        });
    }
    //...

    private Task getTask(){
        String title = binding.editTextNewTask.getText().toString();
        String details = "";
        if(binding.editTextNewTaskDetails.getVisibility() == View.VISIBLE
                && !binding.editTextNewTaskDetails.getText().toString().isEmpty()) {
            details = binding.editTextNewTaskDetails.getText().toString();
        }
        Task nTask = new Task(title, details);
        return nTask;
    }

    // todo: maybe util methods
    private void disableBtnSaveTask(Button btnSave) {
        btnSave.setEnabled(false);
        btnSave.setTextColor(getResources()
                .getColor(R.color.green1));
    }

    private void enableBtnSaveTask(Button btnSave)  {
        btnSave.setEnabled(true);
        btnSave.setTextColor(getResources()
                .getColor(R.color.blue_button));
    }

    private String dateToString (Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        return format.format(date);
    }
    //...
}
