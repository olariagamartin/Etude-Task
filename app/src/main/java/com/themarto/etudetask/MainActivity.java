package com.themarto.etudetask;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.ActivityMainBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        if (getIntent().getExtras() != null) {
            String taskId = getIntent().getExtras().getString("task_id", "");
            if (!taskId.equals("")) {
                viewModel.selectTask(taskId);
                BottomSheetTaskDetails taskDetails = new BottomSheetTaskDetails();
                taskDetails.show(getSupportFragmentManager(), taskDetails.getTag());
            }
        }
    }

    public void hideBottomNavView () {
        binding.bottomNavView.setVisibility(View.GONE);
    }

    public void showBottomNavView () {
        binding.bottomNavView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        viewModel.closeDB();
        super.onDestroy();
    }
}