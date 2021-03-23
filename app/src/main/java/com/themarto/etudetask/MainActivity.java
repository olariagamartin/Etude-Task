package com.themarto.etudetask;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.themarto.etudetask.databinding.ActivityMainBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetTaskDetails;
import com.themarto.etudetask.viewmodels.SharedViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupBottomNavigationView();
        checkIntentFromNotification();
    }

    private void setupBottomNavigationView() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        // home page
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String home = pref.getString("start_page", "");
        if (home.equals("timeline") && !viewModel.isStartPageLoaded()) {
            NavGraph navGraph = navController.getGraph();
            navGraph.setStartDestination(R.id.timelineFragment);
            navController.setGraph(navGraph);
        }
        viewModel.setStartPageLoaded(true);
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