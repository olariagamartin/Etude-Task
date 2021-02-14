    package com.themarto.etudetask.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.adapters.TaskDoneAdapter;
import com.themarto.etudetask.databinding.FragmentTasksBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetAddTask;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.utils.MyItemTouchHelper;
import com.themarto.etudetask.utils.SwipeToDeleteCallback;
import com.themarto.etudetask.utils.Util;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private SharedViewModel viewModel;
    private Subject currentSubject;
    MyItemTouchHelper itemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemTouchHelper = new MyItemTouchHelper();
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedSubject().observe(getViewLifecycleOwner(), new Observer<Subject>() {
            @Override
            public void onChanged(Subject subject) {
                currentSubject = subject;
                setupRecyclerViewDoneTasks(Util.getDoneTasks(subject.getTaskList()));
                setupRecyclerViewToDoTasks(Util.getToDoTasks(subject.getTaskList()));
                setToolbarBehavior();
                setupDoneTask();
            }
        });
        setupFAB();

    }

    private void setToolbarBehavior(){
        binding.toolbarTask.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarTask.topAppBar);
        String title = currentSubject.getTitle();
        binding.toolbarTask.toolbarLayout.setTitle(title);
        binding.toolbarTask.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(requireContext(), R.color.subject_red));
        binding.toolbarTask.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(requireContext(), R.color.subject_red));
        setHasOptionsMenu(true);
        binding.toolbarTask.toolbarLayout.setOnClickListener(v -> showDialogEditSubject());
    }

    private void setupDoneTask(){
        setTasksCompletedTitle();
        // Done tasks behavior
        if (currentSubject.getDoneSize() == 0) {
            binding.tasksDoneHeader.setVisibility(View.INVISIBLE);
        } else {
            binding.tasksDoneHeader.setVisibility(View.VISIBLE);

            binding.tasksDoneHeader.setOnClickListener(v -> {
                if (binding.recyclerViewDoneTasks.getVisibility() == View.VISIBLE) {
                    binding.recyclerViewDoneTasks.setVisibility(View.GONE);
                    binding.doneTasksHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_down);
                } else {
                    binding.recyclerViewDoneTasks.setVisibility(View.VISIBLE);
                    binding.doneTasksHeaderIcon.setImageResource(R.drawable.ic_keyboard_arrow_up);
                }
            });
        }
    }

    private void setupFAB(){
        binding.fabAddTask.setOnClickListener(v -> showBottomSheetAddTask());
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if ((scrollY - oldScrollY) > 0 && binding.fabAddTask.getVisibility() == View.VISIBLE) {
                    binding.fabAddTask.hide();
                } else if ((scrollY - oldScrollY) < 0 && binding.fabAddTask.getVisibility() != View.VISIBLE) {
                    binding.fabAddTask.show();
                }
            }
        });
    }

    private void setTasksCompletedTitle(){
        String completedTasksCount = getString(R.string.completed_tasks_count, currentSubject.getDoneSize());
        binding.tasksDoneText.setText(completedTasksCount);
    }

    private void setViewBehavior() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_subject:
                showDialogEditSubject();
                return true;
            case R.id.delete_subject:
                showDialogDeleteSubject();
                return true;
            case R.id.delete_completed_tasks:
                deleteCompletedTasks();
                return true;
            case android.R.id.home:
                Navigation.findNavController(binding.getRoot()).navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogEditSubject() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Edit");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(currentSubject.getTitle());
        editTitle.requestFocus(); // required for API 28+
        editTitle.setSelection(editTitle.getText().length());
        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    Subject updateSubject = new Subject(editTitle.getText().toString(), currentSubject.getTaskList());
                    updateSubject.setId(currentSubject.getId());
                    viewModel.updateSubject(updateSubject);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void showBottomSheetAddTask() {
        BottomSheetAddTask bottomSheet = new BottomSheetAddTask();
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }

    public void showDialogDeleteSubject() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("The subject will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete", (dialog, which) -> {
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                    viewModel.deleteSubject();
                    Toast.makeText(requireContext(), "Subject deleted", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void deleteCompletedTasks() {
        viewModel.deleteAllCompletedTasks();
        Snackbar.make(binding.getRoot(), "Completed tasks deleted", Snackbar.LENGTH_SHORT)
                .show();
    }

    private void setupRecyclerViewToDoTasks(List<Task> toDoTasks){
        // To do tasks
        RecyclerView.LayoutManager layoutManagerTask = new LinearLayoutManager(getContext());
        TaskAdapter taskAdapter = new TaskAdapter(toDoTasks, getTaskListener());

        binding.recyclerViewTasks.setLayoutManager(layoutManagerTask);
        binding.recyclerViewTasks.setAdapter(taskAdapter);
        binding.recyclerViewTasks.setHasFixedSize(true);
        binding.recyclerViewTasks.setNestedScrollingEnabled(false);
        itemTouchHelper.setCallback(new SwipeToDeleteCallback(taskAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTasks);
    }

    private TaskAdapter.TaskListener getTaskListener () {
        return new TaskAdapter.TaskListener() {
            @Override
            public void onItemClick(Task task) {
                viewModel.selectTask(task);
                BottomSheetTaskDetails taskDetails = new BottomSheetTaskDetails();
                taskDetails.show(getParentFragmentManager(), taskDetails.getTag());
            }

            @Override
            public void onTaskChecked(Task task) {
                TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeBounds());
                viewModel.setTaskDone(task);
            }

            @Override
            public void onDeleteTask(Task task) {
                Task deletedTask = viewModel.deleteTask(task);
                showUndoSnackbar(deletedTask);
            }
        };
    }

    private void showUndoSnackbar(Task deletedTask) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Task deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete(deletedTask));
        snackbar.show();
    }

    private void undoDelete(Task deletedTask) {
        TransitionManager.beginDelayedTransition(binding.recyclerViewTasks, new ChangeBounds());
        viewModel.addTask(deletedTask);
    }

    private void setupRecyclerViewDoneTasks(List<Task> doneTasks){
        // Done tasks
        RecyclerView.LayoutManager layoutManagerDone = new LinearLayoutManager(getContext());
        TaskDoneAdapter taskDoneAdapter = new TaskDoneAdapter(doneTasks, task -> {
            TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeBounds());
            viewModel.setTaskUndone(task);
        });
        binding.recyclerViewDoneTasks.setLayoutManager(layoutManagerDone);
        binding.recyclerViewDoneTasks.setAdapter(taskDoneAdapter);
        binding.recyclerViewDoneTasks.setHasFixedSize(true);
        binding.recyclerViewDoneTasks.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}