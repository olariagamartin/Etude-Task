package com.themarto.etudetask.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.adapters.ChapterAdapter;
import com.themarto.etudetask.databinding.FragmentChapterBinding;
import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

public class ChapterFragment extends Fragment {

    private SharedViewModel viewModel;

    private FragmentChapterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChapterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.fabAddChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetAddChapter();
            }
        });

        // Show list of Signatures
        binding.bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSignatures bottomSheet = new BottomSheetSignatures();
                bottomSheet.show(getParentFragmentManager(), "TAG");
            }
        });

        //
        binding.bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.more_actions:
                        lunchBottomSheetDialogSettings();
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
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedSignature().observe(getViewLifecycleOwner(), new Observer<Signature>() {
            @Override
            public void onChanged(Signature signature) {
                setViewBehavior();
                loadChapters(signature.getChapterList());
            }
        });

    }

    private void setViewBehavior() {
        // take it in another method
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarChapter.topAppBar);
        String title = viewModel.getSelectedSignature().getValue().getTitle();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void lunchBottomSheetDialogSettings() {
        BottomSheetDialog settings = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);

        View settingsView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_settings, null);

        settingsView.findViewById(R.id.settings_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameSignature();
                settings.dismiss();
            }
        });

        settingsView.findViewById(R.id.settings_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSignature();
                settings.dismiss();
            }
        });

        settingsView.findViewById(R.id.settings_theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(), "Theme", Snackbar.LENGTH_SHORT).show();
                settings.dismiss();
            }
        });

        settings.setContentView(settingsView);
        settings.show();
    }

    private void renameSignature() {
        // TODO: show dialog
        Toast.makeText(getContext(), "Edit Signature", Toast.LENGTH_SHORT).show();
    }

    private void deleteSignature() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Delete Signature") // TODO: add title
                .setMessage("The signature will be deleted")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: set red color
                        //Delete action
                        Toast.makeText(getContext(), "Signature deleted", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    private void showBottomSheetAddChapter() {
        BottomSheetDialog addChapterDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);

        View addChapterView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_chapter, null);

        EditText chapterTitle = addChapterView.findViewById(R.id.edit_text_new_chapter);
        Button btnSaveChapter = addChapterView.findViewById(R.id.btn_save_chapter);

        chapterTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: extract in a method (util)
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    btnSaveChapter.setEnabled(false);
                    btnSaveChapter.setTextColor(getResources()
                            .getColor(R.color.green1));
                } else {
                    btnSaveChapter.setEnabled(true);
                    btnSaveChapter.setTextColor(getResources()
                            .getColor(R.color.blue_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSaveChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = chapterTitle.getText().toString();
                addNewChapter(title);
                addChapterDialog.dismiss();
            }
        });

        addChapterDialog.setContentView(addChapterView);
        addChapterDialog.show();
    }

    private void addNewChapter(String title) {
        Snackbar.make(getView(), title + " added", Snackbar.LENGTH_SHORT).show();
    }

    private void loadChapters(List<Chapter> chapterList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ChapterAdapter chapterAdapter = new ChapterAdapter(chapterList);
        chapterAdapter.setListener(new Util.MyListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: goto method
                viewModel.selectChapter(position);
                NavDirections action = ChapterFragmentDirections.actionChapterFragmentToTasksFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        binding.recyclerViewChapters.setAdapter(chapterAdapter);
        binding.recyclerViewChapters.setLayoutManager(layoutManager);
        binding.recyclerViewChapters.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}