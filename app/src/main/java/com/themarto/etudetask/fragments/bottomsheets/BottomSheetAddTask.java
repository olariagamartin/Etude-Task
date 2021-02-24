package com.themarto.etudetask.fragments.bottomsheets;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.R;
import com.themarto.etudetask.WorkManagerAlarm;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.utils.MyTextWatcher;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.data.SharedViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;

public class BottomSheetAddTask extends BottomSheetDialogFragment {

    private BottomSheetAddTaskBinding binding;
    private SharedViewModel viewModel;
    private Calendar actual;
    private Calendar calendar = Calendar.getInstance();
    private String flagColor = Util.FlagColors.NONE;

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
        setBtnAddNoteBehavior();
        setBtnAddFlagBehavior();
        setBtnAddDateBehavior();
        setBtnAddTimeBehavior();
        setBtnSaveTaskBehavior();
        setChipsBehavior();

        disableImageButton(binding.btnAddTaskTime);
    }

    // Behavior methods
    private void setEditTextTitleBehavior() {
        binding.editTextNewTask.requestFocus(); // required for API 28+
        binding.editTextNewTask.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String title = s.toString();
                if (title.isEmpty()) {
                    disableTextButton(binding.btnSaveTask);
                } else {
                    enableTextButton(binding.btnSaveTask);
                }
            }
        });
    }

    private void setBtnAddNoteBehavior() {
        binding.btnAddTaskDetails.setOnClickListener(v -> {
            binding.editTextNewTaskDetails.setVisibility(View.VISIBLE);
            binding.editTextNewTaskDetails.requestFocus();
            setBottomSheetExtended();
        });
    }

    private void setBtnAddFlagBehavior() {
        binding.btnAddFlag.setOnClickListener(v -> showFlagSelector());
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
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
        binding.btnAddTaskDueDate.setVisibility(View.GONE);

        binding.chipAddTaskDueDate.setText(Util.getDateString(calendar.getTime(), requireContext()));

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

    private void notifyTimeSet() {
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskTime.setVisibility(View.VISIBLE);
        binding.btnAddTaskTime.setVisibility(View.GONE);

        binding.chipAddTaskTime
                .setText(Util.getTimeString(calendar.getTime()));
    }

    /**
     * Gathers all loaded data and loads it into a Task object.
     * If an alarm was set then is saved.
     *
     * @return the Task with all data
     */
    private Task getTask() {
        String title = binding.editTextNewTask.getText().toString();
        String details = "";
        if (binding.editTextNewTaskDetails.getVisibility() == View.VISIBLE
                && !binding.editTextNewTaskDetails.getText().toString().isEmpty()) {
            details = binding.editTextNewTaskDetails.getText().toString();
        }
        Task nTask = new Task(title, details, viewModel.getSelectedSubject().getValue());
        nTask.setFlagColor(flagColor);
        if (binding.chipAddTaskDueDate.getVisibility() == View.VISIBLE) {
            if (binding.chipAddTaskTime.getVisibility() == View.VISIBLE) {
                saveAlarm(nTask);
            }
            nTask.setDate(calendar.getTime());
        }
        return nTask;
    }

    private void setBottomSheetExtended() {
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @SuppressLint("RestrictedApi")
    private void showFlagSelector() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.btnAddFlag);
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
                binding.btnAddFlag);
        helper.setForceShowIcon(true);
        helper.show();
    }

    private void selectFlagColor(String rgbColor) {
        if (rgbColor.equals(Util.FlagColors.NONE)) {
            binding.btnAddFlag.setImageResource(R.drawable.ic_flag_outline);
            binding.btnAddFlag.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray4));
        } else {
            binding.btnAddFlag.setImageResource(R.drawable.ic_flag_fill_yellow);
            binding.btnAddFlag.setColorFilter(Color.parseColor(rgbColor));
        }
        flagColor = rgbColor;
    }

    private void disableTextButton(Button btn) {
        btn.setEnabled(false);
        btn.setTextColor(getResources()
                .getColor(R.color.gray1));
    }

    private void enableTextButton(Button btn) {
        btn.setEnabled(true);
        btn.setTextColor(getResources()
                .getColor(R.color.blue_button));
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
}
