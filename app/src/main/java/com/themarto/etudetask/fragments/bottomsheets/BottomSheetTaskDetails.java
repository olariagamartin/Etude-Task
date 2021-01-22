package com.themarto.etudetask.fragments.bottomsheets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.adapters.SubtaskAdapter;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.BottomSheetTaskDetailsBinding;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Subtask;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.utils.MyTextWatcher;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetTaskDetails extends BottomSheetDialogFragment {

    private BottomSheetTaskDetailsBinding binding;
    private SharedViewModel viewModel;
    private Task currentTask;
    private boolean isDeleted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }
        });
    }

    private void setupViewsBehavior(){
        setupSubject(viewModel.getSelectedSubject().getValue());
        setupTaskTitle();
        setupFlagButton();
        setupDateButton();
        setupNotificationButton();
        setupSubtasks();
        setupAddNote();
        setupDeleteButton();
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
        binding.btnTaskDate.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "show date and time picker", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupNotificationButton(){
        // todo: change notification icon
        binding.btnTaskNotification.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Notification active", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSubtasks(){
        loadSubtasks(currentTask.getSubtasks());
        setupAddSubtask();
    }

    private void loadSubtasks(List<Subtask> subtaskList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        SubtaskAdapter adapter = new SubtaskAdapter(subtaskList);
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
            isDeleted = true;
            viewModel.deleteTask();
            dismiss();
        });
    }

    private void showUndoSnackbar(Task deletedTask) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Task deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete(deletedTask));
        snackbar.show();
    }

    private void undoDelete(Task deletedTask) {
        viewModel.addTask(deletedTask);
    }

    private void setupBottomSheet(){
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setPeekHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);
        binding.extraSpace.setMinimumHeight((Resources.getSystem().getDisplayMetrics().heightPixels) / 2);
        BottomSheetBehavior.from(bottomSheetInternal).setFitToContents(false);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private Task getTaskUpdated(){
        Task updatedTask = new Task();
        updatedTask.setId(currentTask.getId());
        updatedTask.setTitle(binding.editTextTaskTitle.getText().toString());
        updatedTask.setNote(binding.editTextTaskNote.getText().toString());
        updatedTask.setSubtasks(currentTask.getSubtasks());
        // todo: flag, date, notification
        return  updatedTask;
    }

    private void saveChanges(){
        /*Task taskUpdated = getTaskUpdated();
        viewModel.updateTask(taskUpdated);*/
        viewModel.commitTaskChanges();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isDeleted) {
            saveChanges();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
