package com.themarto.etudetask.fragments.bottomsheets;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.color.MaterialColors;
import com.themarto.etudetask.R;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.utils.MyTextWatcher;
import com.themarto.etudetask.utils.Util;
import com.themarto.etudetask.viewmodels.AddTaskViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class BottomSheetAddTask extends BottomSheetDialogFragment {

    private static final String ARG_SUBJECT_ID = "SUBJECT_ID";
    private String subjectId;
    private Listener listener;

    private BottomSheetAddTaskBinding binding;
    private AddTaskViewModel viewModel;

    public static BottomSheetAddTask newInstance(String subjectId) {
        BottomSheetAddTask fragment = new BottomSheetAddTask();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_ID, subjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        if (getArguments() != null) {
            subjectId = getArguments().getString(ARG_SUBJECT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddTaskBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(this).get(AddTaskViewModel.class);
        viewModel.loadSubject(subjectId);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViewsBehavior();
        setObservers();
    }

    private void setViewsBehavior() {
        setEditTextTitleBehavior();
        setBtnAddDetailsBehavior();
        setEditTextDetailsBehavior();
        setBtnAddFlagBehavior();
        setBtnAddDateBehavior();
        setBtnAddTimeBehavior();
        setBtnSaveTaskBehavior();
        setChipsBehavior();

        disableImageButton(binding.btnAddTaskTime);
    }

    private void setObservers() {
        viewModel.isSaveBtnActive().observe(getViewLifecycleOwner(), saveBtnActive -> {
            if (saveBtnActive) {
                enableTextButton(binding.btnSaveTask);
            } else {
                disableTextButton(binding.btnSaveTask);
            }
        });

        viewModel.isAddDetailsClicked().observe(getViewLifecycleOwner(), addDetailsClicked -> {
            if (addDetailsClicked) onAddDetailsClicked();
        });

        viewModel.getFlagRgbColor().observe(getViewLifecycleOwner(), this::selectFlagColor);

        viewModel.isDateSet().observe(getViewLifecycleOwner(), dateSet -> {
            if (dateSet) {
                onDateSet();
            } else {
                onDateRemoved();
            }
        });

        viewModel.isTimeSet().observe(getViewLifecycleOwner(), timeSet -> {
            if (timeSet) {
                onTimeSet();
            } else {
                onTimeRemoved();
            }
        });
    }

    private void setEditTextTitleBehavior() {
        binding.editTextNewTask.requestFocus(); // required for API 28+
        binding.editTextNewTask.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onTaskTitleTextChanged(s.toString());
            }
        });
    }

    private void setBtnAddDetailsBehavior() {
        binding.btnAddTaskDetails.setOnClickListener(v -> {
            viewModel.onAddDetailsClicked();
        });
    }

    private void onAddDetailsClicked() {
        binding.editTextNewTaskDetails.setVisibility(View.VISIBLE);
        binding.editTextNewTaskDetails.requestFocus();
        setBottomSheetExtended();
    }

    private void setEditTextDetailsBehavior () {
        binding.editTextNewTaskDetails.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onTaskDetailsChanged(s.toString());
            }
        });
    }

    private void setBtnAddFlagBehavior() {
        binding.btnAddFlag.setOnClickListener(v -> showFlagSelector());
    }

    private void setBtnAddDateBehavior() {
        binding.btnAddTaskDueDate.setOnClickListener(v -> showQuickDateSelector());
    }

    private void setBtnAddTimeBehavior() {
        binding.btnAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void setBtnSaveTaskBehavior() {
        binding.btnSaveTask.setOnClickListener(v -> {
            viewModel.onSaveTaskClicked();
            dismiss();
        });
    }

    private void setChipsBehavior() {
        binding.chipAddTaskDueDate.setOnCloseIconClickListener(v -> {
            viewModel.onRemoveDate();
        });

        binding.chipAddTaskDueDate.setOnClickListener(v -> showQuickDateSelector());

        binding.chipAddTaskTime.setOnCloseIconClickListener(v -> {
            viewModel.onRemoveTime();
        });

        binding.chipAddTaskTime.setOnClickListener(v -> lunchTimePicker());
    }

    private void onDateRemoved() {
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskDueDate.setVisibility(View.GONE);
        binding.btnAddTaskDueDate.setVisibility(View.VISIBLE);
        binding.chipAddTaskTime.performCloseIconClick();
        disableImageButton(binding.btnAddTaskTime);
    }

    private void onTimeRemoved() {
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskTime.setVisibility(View.GONE);
        binding.btnAddTaskTime.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void showQuickDateSelector() {
        MenuBuilder menu = new MenuBuilder(requireContext());
        MenuItem todayItem  = menu.add(R.string.date_format_today).setIcon(R.drawable.ic_today);
        MenuItem tomorrowItem  = menu.add(R.string.date_format_tomorrow).setIcon(R.drawable.ic_tomorrow);
        MenuItem pickDateItem  = menu.add(R.string.pick_date_item).setIcon(R.drawable.ic_custom_date);

        todayItem.setOnMenuItemClickListener(item -> {
            viewModel.onDateSetForToday();
            return true;
        });

        tomorrowItem.setOnMenuItemClickListener(item -> {
            viewModel.onDateSetForTomorrow();
            return true;
        });

        pickDateItem.setOnMenuItemClickListener(item -> {
            lunchDatePicker();
            return true;
        });

        MenuPopupHelper helper = new MenuPopupHelper(requireContext(),
                menu,
                binding.btnAddFlag);
        helper.setForceShowIcon(true);
        helper.show();
    }

    private void lunchDatePicker() {
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    viewModel.onDateSet(dayOfMonth, month, year);
                }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    private void onDateSet() {
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskDueDate.setVisibility(View.VISIBLE);
        binding.btnAddTaskDueDate.setVisibility(View.GONE);

        binding.chipAddTaskDueDate.setText(Util.getDateString(viewModel.getTaskTime(), requireContext()));

        enableImageButton(binding.btnAddTaskTime);
    }

    private void lunchTimePicker() {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMin = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    viewModel.onTimeSet(hourOfDay, minute);
                }, currentHour, currentMin, false);
        timePickerDialog.show();
    }

    private void onTimeSet() {
        TransitionManager.beginDelayedTransition(binding.layoutChips);
        binding.chipAddTaskTime.setVisibility(View.VISIBLE);
        binding.btnAddTaskTime.setVisibility(View.GONE);

        binding.chipAddTaskTime
                .setText(Util.getTimeString(viewModel.getTaskTime()));
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
                        viewModel.onFlagColorSelected(Util.FlagColors.RED);
                        return true;
                    case R.id.flag_yellow:
                        viewModel.onFlagColorSelected(Util.FlagColors.YELLOW);
                        return true;
                    case R.id.flag_blue:
                        viewModel.onFlagColorSelected(Util.FlagColors.BLUE);
                        return true;
                    case R.id.flag_none:
                        viewModel.onFlagColorSelected(Util.FlagColors.NONE);
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
            binding.btnAddFlag.setColorFilter(MaterialColors.getColor(binding.btnAddFlag, R.attr.colorOnSecondary));
        } else {
            binding.btnAddFlag.setImageResource(R.drawable.ic_flag_fill_yellow);
            binding.btnAddFlag.setColorFilter(Color.parseColor(rgbColor));
        }
    }

    private void disableTextButton(Button btn) {
        btn.setEnabled(false);
        btn.setTextColor(MaterialColors.getColor(btn, R.attr.colorPrimarySurface));
    }

    private void enableTextButton(Button btn) {
        btn.setEnabled(true);
        btn.setTextColor(MaterialColors.getColor(btn, R.attr.colorAccent));
    }

    private void disableImageButton(ImageButton btn) {
        btn.setEnabled(false);
        btn.setColorFilter(MaterialColors.getColor(btn, R.attr.colorPrimarySurface));
    }

    private void enableImageButton(AppCompatImageButton btn) {
        btn.setEnabled(true);
        btn.setColorFilter(MaterialColors.getColor(btn, R.attr.colorOnSecondary));
    }

    public interface Listener {
        void onPause();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listener != null) {
            listener.onPause();
        }
    }
}
