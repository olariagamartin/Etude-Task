package com.themarto.etudetask.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.ChapterAdapter;
import com.themarto.etudetask.databinding.FragmentChapterBinding;
import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChapterFragment extends Fragment {

    private SharedViewModel viewModel;

    private FragmentChapterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChapterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.fabAddChapter.setOnClickListener(v -> showBottomSheetAddChapter());

        // Show list of Signatures
        binding.bottomAppBar.setNavigationOnClickListener(v -> {
            BottomSheetSignatures bottomSheet = new BottomSheetSignatures();
            // bottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
            bottomSheet.show(getParentFragmentManager(), "TAG");
        });

        //
        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.more_actions:
                    lunchBottomSheetDialogSettings();
                    return true;
                default:
                    return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedSignature().observe(getViewLifecycleOwner(), signature -> {
            setViewBehavior(signature);
            loadChapters(signature.getChapterList());
        });

    }

    private void setViewBehavior(Signature signature) {
        // take it in another method
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarChapter.topAppBar);
        String title = viewModel.getSelectedSignature().getValue().getTitle();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void lunchBottomSheetDialogSettings() {
        BottomSheetDialog settings = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);

        View settingsView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_settings, null);

        settingsView.findViewById(R.id.settings_rename).setOnClickListener(v -> {
            showDialogRenameSignature();
            settings.dismiss();
        });

        settingsView.findViewById(R.id.settings_delete).setOnClickListener(v -> {
            showDialogDeleteSignature();
            settings.dismiss();
        });

        settingsView.findViewById(R.id.settings_theme).setOnClickListener(v -> {
            Snackbar.make(getView(), "Theme", Snackbar.LENGTH_SHORT).show();
            settings.dismiss();
        });

        settings.setContentView(settingsView);
        settings.show();
    }

    private void showDialogRenameSignature() {
        // TODO: show keyboard
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Rename");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_title, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(viewModel.getSelectedSignature().getValue().getTitle());
        editTitle.setSelection(editTitle.getText().length());
        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    viewModel.changeSignatureTitle(editTitle.getText().toString());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});
        builder.create().show();
    }

    private void showDialogDeleteSignature() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Delete Signature") // TODO: add title
                .setMessage("The signature will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    // TODO: set red color
                    //Delete action
                    viewModel.deleteSignature();
                    Toast.makeText(getContext(), "Signature deleted", Toast.LENGTH_SHORT)
                            .show();
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

        btnSaveChapter.setOnClickListener(v -> {
            String title = chapterTitle.getText().toString();
            addNewChapter(title);
            addChapterDialog.dismiss();
        });

        addChapterDialog.setContentView(addChapterView);
        addChapterDialog.show();
    }

    private void addNewChapter(String title) {
        Chapter chapter = new Chapter(title);
        viewModel.addChapter(chapter);
        Snackbar.make(getView(), title + " added", Snackbar.LENGTH_SHORT).show();
    }

    private void loadChapters(List<Chapter> chapterList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ChapterAdapter chapterAdapter = new ChapterAdapter(chapterList);
        chapterAdapter.setListener(position -> {
            // TODO: goto method
            viewModel.selectChapter(position);
            NavDirections action = ChapterFragmentDirections.actionChapterFragmentToTasksFragment();
            Navigation.findNavController(getView()).navigate(action);
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