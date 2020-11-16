package com.themarto.etudetask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.themarto.etudetask.databinding.ActivityMainBinding;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private SharedViewModel viewModel;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        // TODO: extract string
        viewModel.setStartSubject(sharedPref.getInt("SELECTED_SUBJECT", 0));
    }

}