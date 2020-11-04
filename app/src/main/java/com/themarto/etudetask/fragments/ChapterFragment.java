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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.ChapterAdapter;
import com.themarto.etudetask.databinding.FragmentChapterBinding;
import com.themarto.etudetask.models.Chapter;

import java.util.List;

public class ChapterFragment extends Fragment {

    private FragmentChapterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChapterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        showChapters(Util.getChapterListEx());

        binding.fabAddChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Add Chapter", Toast.LENGTH_SHORT).show();
            }
        });

        binding.bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getParentFragmentManager(), "TAG");
            }
        });

        binding.bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.more_actions:
                        Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewBehavior();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setViewBehavior() {
        // take it in another method
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarChapter.topAppBar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Chapters");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_chapter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_signature:
                renameSignature();
                return true;
            case R.id.delete_signature:
                deleteSignature();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        private void renameSignature () {
            Toast.makeText(getContext(), "Edit Signature", Toast.LENGTH_SHORT).show();
        }

        private void deleteSignature () {
            Toast.makeText(getContext(), "Delete Signature", Toast.LENGTH_SHORT).show();
        }

        private void showChapters (List < Chapter > chapterList) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            ChapterAdapter chapterAdapter = new ChapterAdapter(chapterList);
            binding.recyclerViewChapters.setAdapter(chapterAdapter);
            binding.recyclerViewChapters.setLayoutManager(layoutManager);
            binding.recyclerViewChapters.setHasFixedSize(true);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}