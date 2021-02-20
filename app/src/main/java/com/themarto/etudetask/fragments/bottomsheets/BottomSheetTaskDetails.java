package com.themarto.etudetask.fragments.bottomsheets;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
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
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
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
    private final int CONTENT_HEIGHT_DEFAULT = 762;

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
                //setSpaceAdded();
                setupViewsBehavior();
                loadData();
            }
        });
    }

    private void setupViewsBehavior(){
        setupTaskTitle();
        btnDoneBehavior();
        btnFlagBehavior();
        btnDateBehavior();
        btnNotificationBehavior();
        chipsBehavior();
        setupAddSubtask();
        setupAddNote();
        btnDeleteBehavior();
    }

    private void loadData() {
        loadSubject(currentTask.getSubject());
        loadDoneBtn();
        loadTaskTitle();
        loadFlag();
        loadDateAndTime();
        loadNote();
        loadSubtasks(currentTask.getSubtasks());
    }

    // Load Data Methods
    private void loadSubject(Subject subject){
        // todo: setup color
        binding.textSubjectTitle.setText(subject.getTitle());
    }

    private void loadDoneBtn() {
        if (currentTask.isDone()) {
            binding.btnCheckboxTaskDetails.setImageResource(R.drawable.ic_checkmark_in_circle);
        } else {
            binding.btnCheckboxTaskDetails.setImageResource(R.drawable.ic_checkmark_circle);
        }
    }

    private void loadTaskTitle(){
        binding.editTextTaskTitle.setText(currentTask.getTitle());
    }

    private void loadFlag() {
        if (currentTask.getFlagColor().equals(Util.FlagColors.NONE)) {
            binding.btnTaskFlag.setImageResource(R.drawable.ic_flag_outline);
            binding.btnTaskFlag.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray4));
        } else {
            binding.btnTaskFlag.setImageResource(R.drawable.ic_flag_fill_yellow);
            binding.btnTaskFlag.setColorFilter(Color.parseColor(currentTask.getFlagColor()));
        }
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

    private void loadNote() {
        binding.editTextTaskNote.setText(currentTask.getNote());
    }

    private void loadSubtasks(List<Subtask> subtaskList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        SubtaskAdapter adapter = new SubtaskAdapter(subtaskList);
        adapter.setListener(new SubtaskAdapter.SubtaskListener() {
            @Override
            public void onDoneClick(int position) {
                Subtask subtask = currentTask.getSubtasks().get(position);
                subtask.setDone(!subtask.isDone());
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
    //...

    // Behavior methods
    private void setupTaskTitle(){
        binding.editTextTaskTitle.setOnClickListener(v -> setStateExpanded());
        binding.editTextTaskTitle.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentTask.setTitle(s.toString());
            }
        });
        if (currentTask.isDone()) {
            binding.editTextTaskTitle.setTextColor(getResources().getColor(R.color.gray3));
            binding.editTextTaskTitle.setPaintFlags(
                    binding.editTextTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.editTextTaskTitle.setTextColor(getResources().getColor(R.color.gray2));
            binding.editTextTaskTitle.setPaintFlags(binding.editTextTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void btnDoneBehavior() {
        binding.btnCheckboxTaskDetails.setOnClickListener(v -> {
            currentTask.setDone(!currentTask.isDone());
            binding.chipAddTaskTime.performCloseIconClick();
            viewModel.updateTask(currentTask);
        });
    }

    private void btnFlagBehavior(){
        binding.btnTaskFlag.setOnClickListener(v -> showFlagSelector());
    }

    private void btnDateBehavior(){
        binding.btnAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());
    }

    private void btnNotificationBehavior(){
        binding.btnAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void chipsBehavior() {
        binding.chipAddTaskDueDate.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
            binding.chipAddTaskDueDate.setVisibility(View.GONE);
            binding.btnAddTaskDueDate.setVisibility(View.VISIBLE);
            binding.chipAddTaskTime.performCloseIconClick();
            disableImageButton(binding.btnAddTaskTime);
            currentTask.setDate(null);
        });

        binding.chipAddTaskDueDate.setOnClickListener(v -> lunchDatePicker());

        binding.chipAddTaskTime.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.linearLayoutButtons);
            binding.chipAddTaskTime.setVisibility(View.GONE);
            binding.btnAddTaskTime.setVisibility(View.VISIBLE);
            deleteAlarm(currentTask);
        });

        binding.chipAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void setupAddSubtask(){
        binding.editTextAddSubtask.setOnClickListener(v -> setStateExpanded());
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
        binding.editTextTaskNote.setOnClickListener(v -> setStateExpanded());
        binding.editTextTaskNote.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentTask.setNote(s.toString());
            }
        });
    }

    private void btnDeleteBehavior(){
        binding.btnDeleteTask.setOnClickListener(v -> {
            showDialogDeleteTask();
        });
    }
    //...

    private void setupBottomSheet(){
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setPeekHeight(screenHeight / 2);
        int spaceAdded = (screenHeight - CONTENT_HEIGHT_DEFAULT) - getStatusBarHeight();
        binding.extraSpace.setMinimumHeight(spaceAdded);
        //setSpaceAdded();
        BottomSheetBehavior.from(bottomSheetInternal).setFitToContents(false);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setStateExpanded () {
        // todo
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
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

        currentTask.setDate(calendar.getTime());
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

        currentTask.setDate(calendar.getTime());
        saveAlarm(currentTask);
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

    @SuppressLint("RestrictedApi")
    private void showFlagSelector() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.btnTaskFlag);
        popupMenu.getMenuInflater().inflate(R.menu.menu_flag_selector, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.flag_red:
                        selectFlagColor(Util.FlagColors.RED);
                        return true;
                    case R.id.flag_yellow:
                        selectFlagColor(Util.FlagColors.YELLOW);
                        return true;
                    case R.id.flag_blue:
                        selectFlagColor(Util.FlagColors.BLUE);
                        return true;
                    case R.id.flag_none:
                        selectFlagColor(Util.FlagColors.NONE);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuPopupHelper helper = new MenuPopupHelper(requireContext(),
                (MenuBuilder) popupMenu.getMenu(),
                binding.btnTaskFlag);
        helper.setForceShowIcon(true);
        helper.show();
    }

    private void selectFlagColor(String rgbColor) {
        if (rgbColor.equals(Util.FlagColors.NONE)) {
            binding.btnTaskFlag.setImageResource(R.drawable.ic_flag_outline);
            binding.btnTaskFlag.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray4));
        } else {
            binding.btnTaskFlag.setImageResource(R.drawable.ic_flag_fill_yellow);
            binding.btnTaskFlag.setColorFilter(Color.parseColor(rgbColor));
        }
        currentTask.setFlagColor(rgbColor);
    }

    // todo: extract method in other class
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

    // todo: extract method in other class
    private void deleteAlarm(Task task) {
        if(task.hasAlarm()) {
            WorkManager.getInstance(requireContext()).cancelWorkById(UUID
                    .fromString(task.getAlarmStringId()));
            task.setAlarmStringId("");
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isDeleted) {
            viewModel.commitTaskChanges();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
