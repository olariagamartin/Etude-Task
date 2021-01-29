package com.themarto.etudetask.fragments.bottomsheets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.themarto.etudetask.R;
import com.themarto.etudetask.WorkManagerAlarm;
import com.themarto.etudetask.adapters.SubtaskAdapter;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.BottomSheetTaskDetailsBinding;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Subtask;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.utils.MyTextWatcher;
import com.themarto.etudetask.utils.Util;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.WorkManager;

public class BottomSheetTaskDetails extends BottomSheetDialogFragment {

    private BottomSheetTaskDetailsBinding binding;
    private SharedViewModel viewModel;
    private Task currentTask;
    private boolean isDeleted = false;
    private Calendar actual;
    private Calendar calendar = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTaskDetailsBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setupBottomSheet();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getSelectedTask().observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                currentTask = task;
                setupViewsBehavior();
                // todo: separate behavior from load content
                loadData();
            }
        });
    }

    private void setupViewsBehavior(){
        setupSubject(viewModel.getSelectedSubject().getValue());
        setupTaskTitle();
        setupFlagButton();
        setupDateButton();
        setupNotificationButton();
        setupChips();
        setupSubtasks();
        setupAddNote();
        setupDeleteButton();
    }

    private void loadData() {
        loadDateAndTime();
    }

    private void loadDateAndTime() {
        if (currentTask.getDate() != null) {
            binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
            binding.btnAddTaskDueDate.setVisibility(View.GONE);
            binding.chipAddTaskDueDate.setText(Util.getDateString(currentTask.getDate()));
            enableImageButton(binding.btnAddTaskTime);
            calendar.setTime(currentTask.getDate());
            if (currentTask.hasAlarm()) {
                binding.chipAddTaskTime.setVisibility(View.VISIBLE);
                binding.btnAddTaskTime.setVisibility(View.GONE);
                binding.chipAddTaskTime.setText(Util.getTimeString(currentTask.getDate()));
            }
        }
    }

    private void setupSubject(Subject subject){
        // todo: setup color
        binding.textSubjectTitle.setText(subject.getTitle());
    }

    private void setupTaskTitle(){
        binding.editTextTaskTitle.setText(currentTask.getTitle());
        binding.editTextTaskTitle.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentTask.setTitle(s.toString());
            }
        });
        binding.btnCheckboxTaskDetails.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFlagButton(){
        // todo: show fill flag with color
        binding.btnTaskFlag.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "show flag selector", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDateButton(){
        // todo: show date and time picker, show chip
        binding.btnAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());
    }

    private void setupNotificationButton(){
        binding.btnAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void setupChips() {
        binding.chipAddTaskDueDate.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
            binding.chipAddTaskDueDate.setVisibility(View.GONE);
            binding.btnAddTaskDueDate.setVisibility(View.VISIBLE);
            binding.chipAddTaskTime.performCloseIconClick();
            disableImageButton(binding.btnAddTaskTime);
        });

        binding.chipAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());

        binding.chipAddTaskTime.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
            binding.chipAddTaskTime.setVisibility(View.GONE);
            binding.btnAddTaskTime.setVisibility(View.VISIBLE);
        });

        binding.chipAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void setupSubtasks(){
        loadSubtasks(currentTask.getSubtasks());
        setupAddSubtask();
    }

    private void loadSubtasks(List<Subtask> subtaskList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        SubtaskAdapter adapter = new SubtaskAdapter(subtaskList);
        adapter.setListener(new SubtaskAdapter.SubtaskListener() {
            @Override
            public void onDoneClick(int position) {
                Subtask subtask = currentTask.getSubtasks().get(position);
                if(subtask.isDone())
                    subtask.setDone(false);
                else
                    subtask.setDone(true);
                viewModel.updateTask(currentTask);
            }

            @Override
            public void onDeleteSubtask(int position) {
                currentTask.getSubtasks().remove(position);
                viewModel.updateTask(currentTask);
            }

            @Override
            public TextWatcher afterEditTitle(int position) {
                return new MyTextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        currentTask.getSubtasks().get(position).setTitle(s.toString());
                    }
                };
            }
        });
        binding.recyclerViewSubtasks.setLayoutManager(layoutManager);
        binding.recyclerViewSubtasks.setAdapter(adapter);
    }

    private void setupAddSubtask(){
        binding.editTextAddSubtask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String subtaskTitle = binding.editTextAddSubtask.getText().toString();
                    if(!subtaskTitle.isEmpty()) {
                        currentTask.getSubtasks().add(new Subtask(subtaskTitle));
                        viewModel.updateTask(currentTask);
                        binding.editTextAddSubtask.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setupAddNote(){
        binding.editTextTaskNote.setText(currentTask.getNote());
        binding.editTextTaskNote.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentTask.setNote(s.toString());
            }
        });
    }

    private void setupDeleteButton(){
        binding.btnDeleteTask.setOnClickListener(v -> {
            showDialogDeleteTask();
        });
    }

    private void setupBottomSheet(){
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setPeekHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);
        binding.extraSpace.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);
        BottomSheetBehavior.from(bottomSheetInternal).setFitToContents(false);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    private void notifyDateSet() {
        TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
        binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
        binding.btnAddTaskDueDate.setVisibility(View.GONE);

        binding.chipAddTaskDueDate.setText(Util.getDateString(calendar.getTime()));

        enableImageButton(binding.btnAddTaskTime);
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
        TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
        binding.chipAddTaskTime.setVisibility(View.VISIBLE);
        binding.btnAddTaskTime.setVisibility(View.GONE);

        binding.chipAddTaskTime
                .setText(Util.getTimeString(calendar.getTime()));
    }

    private void disableImageButton(ImageButton btn) {
        btn.setEnabled(false);
        btn.setImageTintList(AppCompatResources.getColorStateList(requireContext(),
                R.color.gray1));
    }

    private void enableImageButton(AppCompatImageButton btn) {
        btn.setEnabled(true);
        btn.setImageTintList(AppCompatResources.getColorStateList(requireContext(),
                R.color.gray4));
    }

    private void showDialogDeleteTask() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("The task will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete", (dialog, which) -> {
                    isDeleted = true;
                    viewModel.deleteTask();
                    dismiss();
                }).show();
    }

    private void setupAlarm() {
        deleteAlarm(currentTask);
        currentTask.setDate(null);

        // i need to find a better way (really!)
        if (binding.chipAddTaskDueDate.getVisibility() == View.VISIBLE) {
            currentTask.setDate(calendar.getTime());
            if(binding.chipAddTaskTime.getVisibility() == View.VISIBLE) {
                saveAlarm(currentTask);
            }
        }
    }

    private void saveAlarm(Task task) {
        // the notification will be launched just at the start of the selected minute
        calendar.set(Calendar.SECOND, 0);
        long alertTime = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (alertTime > 0) {
            String notificationTitle = task.getTitle();
            Subject subject = viewModel.getSelectedSubject().getValue();
            String notificationDetail = subject.getTitle();
            Data data = Util.saveNotificationData(notificationTitle, notificationDetail, task.getId());

            String alarmStringId = WorkManagerAlarm
                    .saveAlarm(alertTime, data, subject.getId(), requireContext());

            task.setAlarmStringId(alarmStringId);
        }
    }

    private void deleteAlarm(Task task) {
        if(task.hasAlarm()) {
            WorkManager.getInstance(requireContext()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
            task.setAlarmStringId("");
        }
    }

    private void commitChanges(){
        viewModel.commitTaskChanges();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isDeleted) {
            setupAlarm();
            commitChanges();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
