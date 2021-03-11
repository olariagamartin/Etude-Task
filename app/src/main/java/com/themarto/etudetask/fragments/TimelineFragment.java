package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.adapters.TaskTimelineAdapter;
import com.themarto.etudetask.data.TimelineViewModel;
import com.themarto.etudetask.databinding.FragmentTimelineBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;
import com.themarto.etudetask.models.Task;
import com.themarto.etudetask.utils.MyItemTouchHelper;
import com.themarto.etudetask.utils.SwipeToDeleteCallbackTimeline;

import java.util.List;

public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;
    private TimelineViewModel viewModel;
    private MyItemTouchHelper todayItemTouchHelper;
    private MyItemTouchHelper upcomingItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimelineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayItemTouchHelper = new MyItemTouchHelper();
        upcomingItemTouchHelper = new MyItemTouchHelper();
        viewModel = ViewModelProviders.of(requireActivity()).get(TimelineViewModel.class);
        viewModel.loadLists();
        setupAppBar();
        setupHeaderTitles();
        viewModel.getTodayTaskList().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                loadTodayTaskList(taskList);
            }
        });
        viewModel.getUpcomingTaskList().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                loadUpcomingTaskList(taskList);
            }
        });
    }

    private void setupAppBar() {
        AppCompatActivity activity = ((AppCompatActivity) requireActivity());
        activity.setSupportActionBar(binding.timelineToolbar);
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        TextView toolbarTitle = activity.getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.timeline_app_bar_title);
    }

    private void setupHeaderTitles () {
        binding.headerToday.textHeaderTimelineList.setText(R.string.timeline_list_header_text_today);
        binding.headerUpcoming.textHeaderTimelineList.setText(R.string.timeline_list_header_text_upcoming);
    }

    private void loadTodayTaskList(List<Task> list) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        TaskTimelineAdapter adapter = new TaskTimelineAdapter(list, getTaskTimelineListener(), false);

        binding.todayTasks.setLayoutManager(layoutManager);
        binding.todayTasks.setAdapter(adapter);
        binding.todayTasks.setHasFixedSize(true);
        binding.todayTasks.setNestedScrollingEnabled(false);
        todayItemTouchHelper.setCallback(new SwipeToDeleteCallbackTimeline(adapter, requireContext()));
        todayItemTouchHelper.attachToRecyclerView(binding.todayTasks);
    }

    private void loadUpcomingTaskList (List<Task> list) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        TaskTimelineAdapter adapter = new TaskTimelineAdapter(list, getTaskTimelineListener(), true);

        binding.upcomingTasks.setLayoutManager(layoutManager);
        binding.upcomingTasks.setAdapter(adapter);
        binding.upcomingTasks.setHasFixedSize(true);
        binding.upcomingTasks.setNestedScrollingEnabled(false);
        upcomingItemTouchHelper.setCallback(new SwipeToDeleteCallbackTimeline(adapter, requireContext()));
        upcomingItemTouchHelper.attachToRecyclerView(binding.upcomingTasks);
    }

    private TaskAdapter.TaskListener getTaskTimelineListener() {
        return new TaskAdapter.TaskListener() {
            @Override
            public void onItemClick(Task task) {
                BottomSheetTaskDetails taskDetails = BottomSheetTaskDetails.newInstance(task.getId());
                taskDetails.setListener((() -> viewModel.loadLists()));
                taskDetails.show(getParentFragmentManager(), taskDetails.getTag());
            }

            @Override
            public void onTaskChecked(Task task) {
                TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeBounds());
                viewModel.setTaskAsDone(task);
                showUndoDoneSnackBar(task);
            }

            @Override
            public void onDeleteTask(Task task) {
                viewModel.deleteTask(task);
                showUndoDeleteSnackBar(task);
            }
        };
    }

    private void showUndoDoneSnackBar(Task doneTask) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.snackbar_task_done_message,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo_action, v -> undoDone(doneTask));
        snackbar.show();
    }

    private void undoDone(Task doneTask) {
        TransitionManager.beginDelayedTransition(binding.todayTasks, new ChangeBounds());
        doneTask.setDone(false);
        viewModel.updateTask(doneTask);
    }

    private void showUndoDeleteSnackBar(Task deletedTask) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.snackbar_task_deleted_message,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo_action, v -> undoDelete(deletedTask));
        snackbar.show();
    }

    private void undoDelete(Task deletedTask) {
        TransitionManager.beginDelayedTransition(binding.getRoot(), new ChangeBounds());
        viewModel.addTask(deletedTask);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}