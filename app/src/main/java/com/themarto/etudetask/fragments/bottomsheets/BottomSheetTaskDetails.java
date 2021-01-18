package com.themarto.etudetask.fragments.bottomsheets;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.BottomSheetTaskDetailsBinding;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

public class BottomSheetTaskDetails extends BottomSheetDialogFragment {

    private BottomSheetTaskDetailsBinding binding;
    private SharedViewModel viewModel;
    private Task currentTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTaskDetailsBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setBottomSheetExtended();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTask = viewModel.getSelectedTask().getValue();
        setupViewsBehavior();
        // setBottomSheetExtended();
        /*viewModel.getSelectedTask().observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                currentTask = task;
                setupViewsBehavior();
            }
        });*/
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
        setupSaveButton();
    }

    private void setupSubject(Subject subject){
        // todo: setup color
        binding.textSubjectTitle.setText(subject.getTitle());
    }

    private void setupTaskTitle(){
        binding.editTextTaskTitle.setText(currentTask.getTitle());
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
        // todo: setup recycler view
        binding.btnAddSubtask.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Add subtask", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAddNote(){

    }

    private void setupDeleteButton(){
        binding.btnDeleteTask.setOnClickListener(v -> {
            viewModel.deleteTask();
            dismiss();
            // todo: show alert dialog
            //showUndoSnackbar(deletedTask);
        });
    }

    private void setupSaveButton(){
        binding.btnSaveTask.setOnClickListener(v -> {
            viewModel.updateTask(getTask());
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

    private Task getTask(){
        Task updatedTask = new Task();
        updatedTask.setId(currentTask.getId());
        updatedTask.setTitle(binding.editTextTaskTitle.getText().toString());
        updatedTask.setDetails(binding.editTextTaskNote.getText().toString());
        return  updatedTask;
    }

    private void setBottomSheetExtended(){
        View bottomSheetInternal = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    
}
