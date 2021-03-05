package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.TaskAdapter;
import com.themarto.etudetask.adapters.TaskTimelineAdapter;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.FragmentTimelineBinding;
import com.themarto.etudetask.models.Task;

import java.util.List;

public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;
    private SharedViewModel viewModel;

    public TimelineFragment() {
        // Required empty public constructor
    }

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
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        setupAppBar();
        setupHeaderTitles();
        viewModel.getTodayTaskList().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                loadTodayTaskList(taskList);
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
        binding.headerToday.textHeaderToday.setText(R.string.timeline_list_header_text_today);
        binding.headerTomorrow.textHeaderToday.setText(R.string.timeline_list_header_text_tomorrow);
        binding.headerUpcoming.textHeaderToday.setText(R.string.timeline_list_header_text_upcoming);
    }

    private void loadTodayTaskList(List<Task> list) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        TaskTimelineAdapter adapter = new TaskTimelineAdapter(list, getTaskTimelineListner(), false);

        binding.todayTasks.setLayoutManager(layoutManager);
        binding.todayTasks.setAdapter(adapter);
        binding.todayTasks.setHasFixedSize(true);
        binding.todayTasks.setNestedScrollingEnabled(false);
    }

    private TaskAdapter.TaskListener getTaskTimelineListner() {
        return new TaskAdapter.TaskListener() {
            @Override
            public void onItemClick(Task task) {
                Toast.makeText(requireContext(), "Item click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTaskChecked(Task task) {
                Toast.makeText(requireContext(), "Task checked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteTask(Task task) {
                Toast.makeText(requireContext(), "task deleted", Toast.LENGTH_SHORT).show();
            }
        };
    }


}