package com.themarto.etudetask;

import android.os.Bundle;
import android.view.View;

import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.ActivityMainBinding;

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
    }

    @Override
    protected void onDestroy() {
        viewModel.closeDB();
        super.onDestroy();
    }
}