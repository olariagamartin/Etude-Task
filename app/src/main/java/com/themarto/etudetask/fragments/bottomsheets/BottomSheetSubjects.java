package com.themarto.etudetask.fragments.bottomsheets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.SubjectAdapter;
import com.themarto.etudetask.databinding.BottomSheetSubjectsBinding;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.data.SharedViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.themarto.etudetask.utils.Util.SELECTED_SUBJECT_KEY;

public class BottomSheetSubjects extends BottomSheetDialogFragment {

    RecyclerView recyclerViewSubjects;

    private BottomSheetSubjectsBinding binding;
    private SharedViewModel viewModel;
    private SharedPreferences sharedPref;

    public BottomSheetSubjects() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSubjectsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        recyclerViewSubjects = binding.recyclerViewSubjects;
        binding.addSubject.setOnClickListener(v -> {
            dismiss();
            showDialogAddSubject();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        //viewModel.getAllSubjects().observe(this, subjectList -> loadSubjects(subjectList));
    }

    // ACTIONS
    private void showDialogAddSubject() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("New Subject");
        View newSubjectLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText subjectTitle = newSubjectLayout.findViewById(R.id.edit_title_dialog);
        subjectTitle.requestFocus(); // required for API 28+
        builder.setView(newSubjectLayout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = subjectTitle.getText().toString();
            if (!title.isEmpty()) {
                saveSubject(title);
                sharedPref.edit()
                        .putInt(SELECTED_SUBJECT_KEY, viewModel.getAllSubjects().getValue().size() - 1)
                        .apply();
            } else {
                Toast.makeText(getContext(), "The name is empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", ((dialog, which) -> { }));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    /*private void loadSubjects(List<Subject> subjectList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        int selectedSubject = sharedPref.getInt(SELECTED_SUBJECT_KEY, 0);
        SubjectAdapter subjectAdapter = new SubjectAdapter(subjectList, position -> {
            sharedPref.edit().putInt(SELECTED_SUBJECT_KEY, position).apply();
            viewModel.selectSubject(position);
            dismiss();
        });
        recyclerViewSubjects.setLayoutManager(layoutManager);
        recyclerViewSubjects.setAdapter(subjectAdapter);
        recyclerViewSubjects.setHasFixedSize(true);
    }*/

    private void saveSubject(String title) {
        Subject subject = new Subject(title);
        viewModel.addSubject(subject);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
