package com.themarto.etudetask.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.SectionAdapter;
import com.themarto.etudetask.databinding.FragmentSectionBinding;
import com.themarto.etudetask.fragments.bottomsheets.BottomSheetSubjects;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.viewmodel.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SectionFragment extends Fragment {

    private SharedViewModel viewModel;

    private FragmentSectionBinding binding;

    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSectionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        binding.fabAddSection.setOnClickListener(v -> showBottomSheetAddSection());

        // Show list of Subjects
        binding.bottomAppBar.setNavigationOnClickListener(v -> {
            BottomSheetSubjects bottomSheet = new BottomSheetSubjects();
            // bottomSheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
            bottomSheet.show(getParentFragmentManager(), "SUBJECT_TAG");
        });

        //
        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.more_actions) {
                lunchBottomSheetDialogSettings();
                return true;
            }
            return false;
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedSubject().observe(getViewLifecycleOwner(), subject -> {
            setViewBehavior(subject);
            loadSections(subject.getSectionList());
        });

    }

    private void setViewBehavior(Subject subject) {
        // take it in another method
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarSection.topAppBar);
        String title = viewModel.getSelectedSubject().getValue().getTitle();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void lunchBottomSheetDialogSettings() {
        BottomSheetDialog settings = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);

        View settingsView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_settings, null);

        settingsView.findViewById(R.id.settings_rename).setOnClickListener(v -> {
            showDialogRenameSubject();
            settings.dismiss();
        });

        settingsView.findViewById(R.id.settings_delete).setOnClickListener(v -> {
            showDialogDeleteSubject();
            settings.dismiss();
        });

        settingsView.findViewById(R.id.settings_theme).setOnClickListener(v -> {
            Snackbar.make(getView(), "Theme", Snackbar.LENGTH_SHORT).show();
            settings.dismiss();
        });

        settings.setContentView(settingsView);
        settings.show();
    }

    private void showDialogRenameSubject() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Rename");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_title, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(viewModel.getSelectedSubject().getValue().getTitle());
        editTitle.setSelection(editTitle.getText().length());
        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    if (editTitle.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "The name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        viewModel.changeSubjectTitle(editTitle.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void showDialogDeleteSubject() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Delete Subject") // TODO: add title
                .setMessage("The subject will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    // TODO: set red color
                    //Delete action
                    if (!viewModel.deleteSubject()) {
                        // Todo: extract string
                        Snackbar.make(binding.getRoot(), "You must have at least one subject",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    } else {
                        sharedPref.edit().putInt("SELECTED_SUBJECT", 0).apply();

                        Toast.makeText(requireContext(), "Subject deleted",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    private void showBottomSheetAddSection() {
        BottomSheetDialog addSectionDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);

        View addSectionView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_section, null);

        EditText sectionTitle = addSectionView.findViewById(R.id.edit_text_new_section);
        Button btnSaveSection = addSectionView.findViewById(R.id.btn_save_section);

        sectionTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: extract in a method (util)
                String title = s.toString();
                // TODO: add condition on start with ' '
                if (title.isEmpty()) {
                    btnSaveSection.setEnabled(false);
                    btnSaveSection.setTextColor(getResources()
                            .getColor(R.color.green1));
                } else {
                    btnSaveSection.setEnabled(true);
                    btnSaveSection.setTextColor(getResources()
                            .getColor(R.color.amber_600));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSaveSection.setOnClickListener(v -> {
            String title = sectionTitle.getText().toString();
            addNewSection(title);
            addSectionDialog.dismiss();
        });

        addSectionDialog.setContentView(addSectionView);
        addSectionDialog.show();
    }

    private void addNewSection(String title) {
        Section section = new Section(title);
        viewModel.addSection(section);
    }

    private void loadSections(List<Section> sectionList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        SectionAdapter sectionAdapter = new SectionAdapter(sectionList);
        sectionAdapter.setListener(position -> {
            // TODO: goto method
            viewModel.selectSection(position);
            NavDirections action = SectionFragmentDirections.actionSectionFragmentToTasksFragment();
            Navigation.findNavController(getView()).navigate(action);
        });
        binding.recyclerViewSections.setAdapter(sectionAdapter);
        binding.recyclerViewSections.setLayoutManager(layoutManager);
        binding.recyclerViewSections.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}