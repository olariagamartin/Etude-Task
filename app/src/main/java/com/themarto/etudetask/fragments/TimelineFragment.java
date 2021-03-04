package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.themarto.etudetask.R;
import com.themarto.etudetask.databinding.FragmentTimelineBinding;

public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;

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
        setupAppBar();
        setupHeaderTitles();
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
}