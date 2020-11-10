package com.themarto.etudetask.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.databinding.BottomSheetAddTaskBinding;
import com.themarto.etudetask.databinding.FragmentTasksBinding;
import com.themarto.etudetask.models.Task;

import java.util.List;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        showTasks(Util.getTaskListExample());

        setViewBehavior();

        return view;
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
        binding.toolbarTask.toolbarLayout.setTitle("My Tasks");
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
            case R.id.rename_chapter:
                renameChapter();
                return true;
            case R.id.delete_chapter:
                deleteChapter();
                return true;
            case android.R.id.home:
                Navigation.findNavController(getView()).navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showBottomSheetAddTask() {
        BottomSheetDialog addTaskDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);

        View addTaskView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_task, null);

        BottomSheetAddTaskBinding addTaskBinding = BottomSheetAddTaskBinding.bind(addTaskView);

        addTaskBinding.editTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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
            public void afterTextChanged(Editable s) {

            }
        });

        addTaskBinding.btnDueDateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
                addTaskBinding.btnDueDateTask.setVisibility(View.GONE);
                addTaskBinding.chipDueDate.setVisibility(View.VISIBLE);
            }
        });

        addTaskBinding.btnNotificationAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
                addTaskBinding.btnNotificationAddTask.setVisibility(View.GONE);
                addTaskBinding.chipNotification.setVisibility(View.VISIBLE);
            }
        });

        addTaskBinding.chipDueDate.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
                addTaskBinding.btnDueDateTask.setVisibility(View.VISIBLE);
                addTaskBinding.chipDueDate.setVisibility(View.GONE);
            }
        });

        addTaskBinding.chipNotification.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(addTaskBinding.layoutChips, new AutoTransition());
                addTaskBinding.btnNotificationAddTask.setVisibility(View.VISIBLE);
                addTaskBinding.chipNotification.setVisibility(View.GONE);
            }
        });

        addTaskBinding.btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = addTaskBinding.editTextNewTask.getText().toString();
                addNewTask(title);
                addTaskDialog.dismiss();
            }
        });

        addTaskDialog.setContentView(addTaskView);
        addTaskDialog.show();

    }

    private void addNewTask(String title) {
        Snackbar.make(getView(), title+" added", Snackbar.LENGTH_SHORT).show();
    }

    public void renameChapter() {
        Toast.makeText(getContext(), "Edit Chapter", Toast.LENGTH_SHORT).show();
    }

    public void deleteChapter() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Delete this chapter") // TODO: add title
                .setMessage("The chapter will be deleted")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete action
                        Toast.makeText(getContext(), "Chapter deleted", Toast.LENGTH_SHORT)
                                .show();
                    }
                }).show();
    }

    private void showTasks(List<Task> taskList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        TaskAdapter taskAdapter = new TaskAdapter(taskList);
        binding.recyclerViewTasks.setLayoutManager(layoutManager);
        binding.recyclerViewTasks.setAdapter(taskAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}