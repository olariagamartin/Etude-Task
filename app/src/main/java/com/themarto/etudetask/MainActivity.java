package com.themarto.etudetask;

import android.os.Bundle;
import android.view.View;

import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.ActivityMainBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupBottomNavigationView();
        checkIntentFromNotification();
    }

    private void setupBottomNavigationView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }

    private void checkIntentFromNotification () {
        if (getIntent().getExtras() != null) {
            String taskId = getIntent().getExtras().getString("task_id", "");
            if (!taskId.equals("")) {
                BottomSheetTaskDetails taskDetails = BottomSheetTaskDetails.newInstance(taskId);
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

}