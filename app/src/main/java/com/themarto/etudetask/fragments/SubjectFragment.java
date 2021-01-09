package com.themarto.etudetask.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.SubjectAdapter;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.databinding.FragmentSubjectBinding;
import com.themarto.etudetask.models.Subject;

import java.util.ArrayList;
import java.util.List;

import static com.themarto.etudetask.utils.Util.SELECTED_SUBJECT_KEY;

public class SubjectFragment extends Fragment {

    private FragmentSubjectBinding binding;
    private SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
        viewModel.getAllSubjects().observe(getViewLifecycleOwner(), new Observer<List<Subject>>() {
            @Override
            public void onChanged(List<Subject> subjects) {
                loadSubjects(subjects);
            }
        });
        setupAppBar("Subjects");
        setHasOptionsMenu(true);
    }

    private void setupAppBar(String title) {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.subjectToolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar_subject, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_subject_item){
            showDialogAddSubject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSubjects(List<Subject> subjects){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        SubjectAdapter adapter = new SubjectAdapter(subjects, getSubjectActions());
        binding.recyclerViewSubjects.setAdapter(adapter);
        binding.recyclerViewSubjects.setLayoutManager(layoutManager);
        binding.recyclerViewSubjects.setHasFixedSize(true);
    }

    private SubjectAdapter.SubjectListener getSubjectActions() {
        return new SubjectAdapter.SubjectListener() {
            @Override
            public void onItemClick(int position) {
                viewModel.selectSubject(position);
                NavDirections action = SubjectFragmentDirections.actionSubjectFragmentToTasksFragment();
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }

            @Override
            public void onEditSubjectClick(int position) {
                Subject subject = viewModel.getAllSubjects().getValue().get(position);
                showDialogEditSubject(subject);
            }

            @Override
            public void onDeleteSubjectClick(int position) {
                showDialogDeleteSubject(position);
            }
        };
    }

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

    private void showDialogEditSubject(Subject subject) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Edit");
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(subject.getTitle());
        editTitle.requestFocus(); // required for API 28+
        editTitle.setSelection(editTitle.getText().length());
        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    Subject updateSubject = new Subject(editTitle.getText().toString());
                    updateSubject.setId(subject.getId());
                    viewModel.updateSubject(updateSubject);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public void showDialogDeleteSubject(int position) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("The subject will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete", (dialog, which) -> {
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                    viewModel.deleteSubject(position);
                    Toast.makeText(requireContext(), "Subject deleted", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void saveSubject(String title) {
        Subject subject = new Subject(title);
        viewModel.addSubject(subject);
    }

}