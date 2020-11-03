package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.TaskAdapter;
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

    private void setViewBehavior(){
        binding.recyclerViewTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && binding.fabAddTask.getVisibility() == View.VISIBLE) {
                    binding.fabAddTask.hide();
                } else if (dy < 0 && binding.fabAddTask.getVisibility() != View.VISIBLE) {
                    binding.fabAddTask.show();
                }
            }
        });

        binding.toolbarTask.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.rename_chapter:
                        renameChapter();
                        return true;
                    case R.id.delete_chapter:
                        deleteChapter();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void renameChapter(){
        Toast.makeText(getContext(), "Edit Chapter", Toast.LENGTH_SHORT).show();
    }

    public void deleteChapter(){
        Toast.makeText(getContext(), "Delete Chapter", Toast.LENGTH_SHORT).show();
    }

    private void showTasks(List<Task> taskList){
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