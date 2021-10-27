    package com.themarto.etudetask.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.MainActivity;
import com.themarto.etudetask.R;
import com.themarto.etudetask.viewmodels.SubjectViewModel;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.adapters.TaskDoneAdapter;
import com.themarto.etudetask.databinding.FragmentTasksBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetAddTask;
import com.themarto.etudetask.utils.MyItemTouchHelper;
import com.themarto.etudetask.utils.SwipeToDeleteCallback;
import com.themarto.etudetask.utils.Util;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

public class TasksFragment extends Fragment {

    private String subject_id;
    private Subject currentSubject;

    private FragmentTasksBinding binding;
    private SubjectViewModel viewModel;
    private MyItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subject_id = TasksFragmentArgs.fromBundle(getArguments()).getSubjectId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        ((MainActivity)requireActivity()).hideBottomNavView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemTouchHelper = new MyItemTouchHelper();
        viewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        viewModel.setSubjectId(subject_id);
        viewModel.getSubject().observe(getViewLifecycleOwner(), new Observer<Subject>() {
            @Override
            public void onChanged(Subject subject) {
                currentSubject = subject;
                setupRecyclerViewDoneTasks(Util.getDoneTasks(subject.getTaskList()));
                setupRecyclerViewToDoTasks(Util.getToDoTasks(subject.getTaskList()));
                setToolbarBehavior();
                setupDoneTasks();
            }
        });
        setupFAB();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)requireActivity()).showBottomNavView();
    }

    private void setToolbarBehavior(){
        binding.toolbarTask.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarTask.topAppBar);
        String title = currentSubject.getTitle();
        binding.toolbarTask.toolbarLayout.setTitle(title);
        binding.toolbarTask.toolbarLayout.setCollapsedTitleTextColor(currentSubject.getColor());
        binding.toolbarTask.toolbarLayout.setExpandedTitleColor(currentSubject.getColor());
        setHasOptionsMenu(true);
        binding.toolbarTask.toolbarLayout.setOnClickListener(v -> showDialogEditSubject());
    }

    private void setupDoneTasks(){
        setTasksCompletedTitle();
        // if there's no done tasks to show
        if (currentSubject.getDoneSize() == 0) {
            binding.tasksDoneHeader.setVisibility(View.INVISIBLE);
        } else {
            binding.tasksDoneHeader.setVisibility(View.VISIBLE);

            // show or hide done task list by pressing the taskDoneHeader
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
        binding.nestedScrollViewTasks.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
        builder.setTitle(R.string.dialog_edit_subject_dialog_title);
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(currentSubject.getTitle());
        editTitle.requestFocus(); // required for API 28+
        editTitle.setSelection(editTitle.getText().length());
        RadioGroup colorPicker = editLayout.findViewById(R.id.radio_group_color_picker);
        final int[] colorPicked = new int[1];
        colorPicked[0] = currentSubject.getColor();
        colorPicker.setOnCheckedChangeListener((group, checkedId) -> {
            colorPicked[0] = group.findViewById(checkedId).getBackgroundTintList().getDefaultColor();
            editTitle.setTextColor(colorPicked[0]);
            editTitle.getBackground().setTint(colorPicked[0]);
        });
        // to select the color that the current subject contain
        String radioBtnTag = Integer.toHexString(colorPicked[0]).substring(2).toUpperCase();
        RadioButton radioBtnChecked = colorPicker.findViewWithTag(radioBtnTag);
        colorPicker.check(radioBtnChecked.getId());
        builder.setView(editLayout)
                .setPositiveButton(R.string.text_button_save, (dialog, which) -> {
                    currentSubject.setTitle(editTitle.getText().toString());
                    currentSubject.setColor(colorPicked[0]);
                    viewModel.updateSubject(currentSubject);
                })
                .setNegativeButton(R.string.text_button_cancel, (dialog, which) -> {});

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            // scroll to the position of the color of the subject
            int x = radioBtnChecked.getLeft();
            int y = radioBtnChecked.getTop();
            editLayout.findViewById(R.id.horizontalScrollView).scrollTo(x, y);
        });
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void showBottomSheetAddTask() {
        BottomSheetAddTask addTask = BottomSheetAddTask.newInstance(this.subject_id);
        // sends the action to perform when the Bottom sheet closes
        addTask.setListener(() -> viewModel.loadSubject());
        addTask.show(getParentFragmentManager(), addTask.getTag());
    }

    private void showDialogDeleteSubject() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle(R.string.alert_dialog_confirmation_title)
                .setMessage(R.string.alert_dialog_delete_subject_message)
                .setNegativeButton(R.string.text_button_cancel, (dialog, which) -> { })
                .setPositiveButton(R.string.text_button_delete, (dialog, which) -> {
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                    viewModel.deleteSubject();
                    Toast.makeText(requireContext(), R.string.toast_subject_deleted, Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void deleteCompletedTasks() {
        viewModel.deletedCompletedTasks();
        Snackbar.make(binding.getRoot(), R.string.snackbar_completed_tasks_deleted, Snackbar.LENGTH_SHORT)
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

    /**
     * Returns the actions that can be performed on each item
     * @return interface whit those actions
     */
    private TaskAdapter.TaskListener getTaskListener () {
        return new TaskAdapter.TaskListener() {
            @Override
            public void onItemClick(Task task) {
                BottomSheetTaskDetails fragment = BottomSheetTaskDetails.newInstance(task.getId());
                fragment.setListener(() -> viewModel.loadSubject());
                fragment.show(getParentFragmentManager(), fragment.getTag());
            }

            @Override
            public void onTaskChecked(Task task) {
                TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeBounds());
                viewModel.setTaskAsDone(task);
            }

            @Override
            public void onDeleteTask(Task task) {
                viewModel.deleteTask(task);
                showUndoSnackbar(task);
            }
        };
    }

    private void showUndoSnackbar(Task deletedTask) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.snackbar_task_deleted_message,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo_action, v -> undoDelete(deletedTask));
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
            task.setDone(false);
            viewModel.updateTask(task);
        });
        binding.recyclerViewDoneTasks.setLayoutManager(layoutManagerDone);
        binding.recyclerViewDoneTasks.setAdapter(taskDoneAdapter);
        binding.recyclerViewDoneTasks.setHasFixedSize(true);
        binding.recyclerViewDoneTasks.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}