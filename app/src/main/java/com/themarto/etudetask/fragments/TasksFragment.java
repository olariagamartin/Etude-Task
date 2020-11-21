package com.themarto.etudetask.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
                // todo: establish a current section
                loadTasks(section);
                setViewBehavior(section);
            }
        });
    }

    private void setViewBehavior(Section section) {
        // On Scroll behavior
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

        // Toolbar behavior
        binding.toolbarTask.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarTask.topAppBar);
        String title = section.getTitle();
        binding.toolbarTask.toolbarLayout.setTitle(title);
        setHasOptionsMenu(true);

        // Tasks completed title
        binding.tasksDoneText.setText("Completed (" + section.getTaskDoneList().size() + ")");

        // FAB behavior
        binding.fabAddTask.setOnClickListener(v -> showBottomSheetAddTask());

        // Done tasks behavior
        if (section.getTaskDoneList().isEmpty()) {
            binding.tasksDoneText.setVisibility(View.GONE);
            binding.recyclerViewDoneTasks.setVisibility(View.GONE);
        } else {
            binding.tasksDoneText.setVisibility(View.VISIBLE);
            binding.recyclerViewDoneTasks.setVisibility(View.VISIBLE);

            binding.tasksDoneText.setOnClickListener(v -> {
                if (binding.recyclerViewDoneTasks.getVisibility() == View.VISIBLE) {
                    binding.recyclerViewDoneTasks.setVisibility(View.GONE);
                } else {
                    binding.recyclerViewDoneTasks.setVisibility(View.VISIBLE);
                }
            });
        }
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
            case R.id.delete_completed_tasks:
                deleteCompletedTasks();
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

    // TODO: divide, maybe use a bottom sheet fragment
    private void showBottomSheetAddTask() {
        BottomSheetDialog addTaskDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);
        Calendar actual = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        View addTaskView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_task, null);

        BottomSheetAddTaskBinding addTaskBinding = BottomSheetAddTaskBinding.bind(addTaskView);

        setEditTextNewTaskBehavior(addTaskBinding);
        setEditTextDetailsBehavior(addTaskBinding);

        addTaskBinding.btnAddTaskDueDate.setOnClickListener(v -> {
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

                            showView(addTaskBinding.chipAddTaskDueDate);
                            hideView(addTaskBinding.btnAddTaskDueDate);
                            addTaskBinding.chipAddTaskDueDate.setText(dateToString(calendar.getTime()));
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        addTaskBinding.btnAddTaskTime.setOnClickListener(v -> {
            int hour = actual.get(Calendar.HOUR_OF_DAY);
            int min = actual.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view1, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            showView(addTaskBinding.chipAddTaskTime);
                            hideView(addTaskBinding.btnAddTaskTime);
                            addTaskBinding.chipAddTaskTime
                                    .setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, hour, min, false);
            timePickerDialog.show();
        });

        addTaskBinding.btnSaveTask.setOnClickListener(v -> {
            // todo: get task data
            String title = addTaskBinding.editTextNewTask.getText().toString();
            String details = "";
            if(addTaskBinding.editTextNewTaskDetails.getVisibility() == View.VISIBLE
            && !addTaskBinding.editTextNewTaskDetails.getText().toString().isEmpty()) {
                details = addTaskBinding.editTextNewTaskDetails.getText().toString();
            }
            //...
            // todo: save task
            Task nTask = new Task(title, details);
            viewModel.addTask(nTask);
            //...
            addTaskDialog.dismiss();
        });

        addTaskDialog.setContentView(addTaskView);
        addTaskDialog.show();
    }

    private void setEditTextNewTaskBehavior(BottomSheetAddTaskBinding addTaskBinding) {
        addTaskBinding.editTextNewTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    disableBtnSaveTask(addTaskBinding.btnSaveTask);
                } else {
                    enableBtnSaveTask(addTaskBinding.btnSaveTask);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

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

    private void setEditTextDetailsBehavior(BottomSheetAddTaskBinding addTaskBinding) {
        addTaskBinding.btnAddTaskDetails.setOnClickListener(v -> {
            showView(addTaskBinding.editTextNewTaskDetails);
            addTaskBinding.editTextNewTaskDetails.requestFocus();
        });
    }
    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    // todo: util method
    private String dateToString (Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        return format.format(date);
    }

    // TODO: add date and reminder

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

    private void deleteCompletedTasks() {
        viewModel.deleteAllCompletedTasks();
        // Todo: study snack bar
        Snackbar.make(binding.getRoot(), "Completed tasks deleted", Snackbar.LENGTH_SHORT)
                .show();
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
        taskDoneAdapter.setListener(new TaskDoneAdapter.TaskDoneListener() {
            @Override
            public void onBtnDoneClick(int position) {
                viewModel.setTaskUndone(position);
            }
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