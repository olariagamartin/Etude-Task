package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.adapters.TaskDoneAdapter;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.databinding.FragmentTasksBinding;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;

    private SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedSection().observe(getViewLifecycleOwner(), new Observer<Section>() {
            @Override
            public void onChanged(Section section) {
                loadTasks(section);
                setViewBehavior();
            }
        });
    }

    private void setViewBehavior() {
        // TODO: take it in another method
        binding.recyclerViewTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && binding.fabAddTask.getVisibility() == View.VISIBLE) {
                    binding.fabAddTask.hide();
                } else if (dy < 0 && binding.fabAddTask.getVisibility() != View.VISIBLE) {
                    binding.fabAddTask.show();
                }
            }
        });

        // take it in another method
        binding.toolbarTask.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarTask.topAppBar);
        String title = viewModel.getSelectedSection().getValue().getTitle();
        binding.toolbarTask.toolbarLayout.setTitle(title);
        setHasOptionsMenu(true);

        binding.fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetAddTask();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_section:
                showDialogRenameSection();
                return true;
            case R.id.delete_section:
                showDialogDeleteSection();
                return true;
            case android.R.id.home:
                Navigation.findNavController(getView()).navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogRenameSection() {
        // TODO: show keyboard
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setTitle("Rename");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_title, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(viewModel.getSelectedSection().getValue().getTitle());
        editTitle.setSelection(editTitle.getText().length());
        dialogBuilder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    viewModel.changeSectionTitle(editTitle.getText().toString());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        dialogBuilder.create().show();
    }

    // TODO: divide
    private void showBottomSheetAddTask() {
        BottomSheetDialog addTaskDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);

        View addTaskView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_task, null);

        BottomSheetAddTaskBinding addTaskBinding = BottomSheetAddTaskBinding.bind(addTaskView);

        addTaskBinding.editTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    addTaskBinding.btnSaveTask.setEnabled(false);
                    addTaskBinding.btnSaveTask.setTextColor(getResources()
                            .getColor(R.color.green1));
                } else {
                    addTaskBinding.btnSaveTask.setEnabled(true);
                    addTaskBinding.btnSaveTask.setTextColor(getResources()
                            .getColor(R.color.blue_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        addTaskBinding.btnDueDateTask.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
            addTaskBinding.btnDueDateTask.setVisibility(View.GONE);
            addTaskBinding.chipDueDate.setVisibility(View.VISIBLE);
        });

        addTaskBinding.btnNotificationAddTask.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
            addTaskBinding.btnNotificationAddTask.setVisibility(View.GONE);
            addTaskBinding.chipNotification.setVisibility(View.VISIBLE);
        });

        addTaskBinding.chipDueDate.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
            addTaskBinding.btnDueDateTask.setVisibility(View.VISIBLE);
            addTaskBinding.chipDueDate.setVisibility(View.GONE);
        });

        addTaskBinding.chipNotification.setOnCloseIconClickListener(v -> {
            TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
            addTaskBinding.btnNotificationAddTask.setVisibility(View.VISIBLE);
            addTaskBinding.chipNotification.setVisibility(View.GONE);
        });

        addTaskBinding.btnSaveTask.setOnClickListener(v -> {
            String title = addTaskBinding.editTextNewTask.getText().toString();
            addNewTask(title);
            addTaskDialog.dismiss();
        });

        addTaskDialog.setContentView(addTaskView);
        addTaskDialog.show();
    }

    // TODO: add date and reminder
    private void addNewTask(String title) {
        Task task = new Task(title);
        viewModel.addTask(task);
    }

    public void showDialogDeleteSection() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Are you sure?") // TODO: add title
                .setMessage("The section will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //
                })
                .setPositiveButton("Delete", (dialog, which) -> { // todo: red button
                    viewModel.deleteSection();
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                }).show();
    }

    private void loadTasks(Section section) {
        // To to tasks
        RecyclerView.LayoutManager layoutManagerTask = new LinearLayoutManager(getContext());
        TaskAdapter taskAdapter = new TaskAdapter(section.getTaskList());
        // Todo: refactor listeners
        taskAdapter.setListener(new Util.MyListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: goto method
                viewModel.selectTask(position);
                NavDirections action = TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment();
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        });
        taskAdapter.setTaskListener(new TaskAdapter.TaskListener() {
            @Override
            public void onTaskChecked(int position) {
                viewModel.setTaskDone(position);
            }
        });
        binding.recyclerViewTasks.setLayoutManager(layoutManagerTask);
        binding.recyclerViewTasks.setAdapter(taskAdapter);
        binding.recyclerViewTasks.setHasFixedSize(true);
        binding.recyclerViewTasks.setNestedScrollingEnabled(false);

        // Done tasks
        RecyclerView.LayoutManager layoutManagerDone = new LinearLayoutManager(getContext());
        TaskDoneAdapter taskDoneAdapter = new TaskDoneAdapter(section.getTaskDoneList());
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