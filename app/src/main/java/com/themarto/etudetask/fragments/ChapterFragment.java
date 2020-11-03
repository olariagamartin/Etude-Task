package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.themarto.etudetask.BottomSheet;
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

        binding.extendedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getParentFragmentManager(), "TAG");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTopAppBarTitle("Calculus");

        setViewBehavior();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setTopAppBarTitle(String title){
        binding.toolbarChapter.topAppBar.setTitle(title);
    }

    private void setViewBehavior(){
        binding.toolbarChapter.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.rename_signature:
                        renameSignature();
                        return true;
                    case R.id.delete_signature:
                        deleteSignature();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void renameSignature(){
        Toast.makeText(getContext(), "Edit Signature", Toast.LENGTH_SHORT).show();
    }

    public void deleteSignature(){
        Toast.makeText(getContext(), "Delete Signature", Toast.LENGTH_SHORT).show();
    }

    private void showChapters(List<Chapter> chapterList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ChapterAdapter chapterAdapter = new ChapterAdapter(chapterList);
        binding.recyclerViewChapters.setAdapter(chapterAdapter);
        binding.recyclerViewChapters.setLayoutManager(layoutManager);
        binding.recyclerViewChapters.setHasFixedSize(true);
    }
}