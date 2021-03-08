package com.themarto.etudetask.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.themarto.etudetask.R;
import com.themarto.etudetask.adapters.SubjectAdapter;
import com.themarto.etudetask.data.SharedViewModel;
import com.themarto.etudetask.data.SubjectListViewModel;
import com.themarto.etudetask.databinding.FragmentSubjectBinding;
import com.themarto.etudetask.models.Subject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectFragment extends Fragment {

    private FragmentSubjectBinding binding;
    private SubjectListViewModel viewModel;
    private List<Subject> subjectList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SubjectListViewModel.class);
        viewModel.getSubjectList().observe(getViewLifecycleOwner(), new Observer<List<Subject>>() {
            @Override
            public void onChanged(List<Subject> subjects) {
                loadSubjects(subjects);
                subjectList = subjects;
            }
        });
        setupAppBar();
        setHasOptionsMenu(true);
    }

    private void setupAppBar() {
        AppCompatActivity activity = ((AppCompatActivity) requireActivity());
        activity.setSupportActionBar(binding.subjectToolbar);
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        TextView toolbarTitle = activity.getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.subjects_app_bar_title);
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
            public void onItemClick(Subject subject) {
                NavDirections action = SubjectFragmentDirections
                        .actionSubjectFragmentToTasksFragment(subject.getId());
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }

            @Override
            public void onEditSubjectClick(int position) {
                Subject subject = subjectList.get(position);
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
        builder.setTitle(R.string.dialog_add_subject_dialog_title);
        View newSubjectLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText subjectTitle = newSubjectLayout.findViewById(R.id.edit_title_dialog);
        subjectTitle.requestFocus(); // required for API 28+
        RadioGroup colorPicker = newSubjectLayout.findViewById(R.id.radio_group_color_picker);
        // variable used on lambda expression need to be final
        final int[] colorPicked = new int[1];
        colorPicked[0] = colorPicker.findViewById(R.id.color_default).getBackgroundTintList().getDefaultColor();
        colorPicker.setOnCheckedChangeListener((group, checkedId) -> {
            colorPicked[0] = group.findViewById(checkedId).getBackgroundTintList().getDefaultColor();
            subjectTitle.setTextColor(colorPicked[0]);
            subjectTitle.getBackground().setTint(colorPicked[0]);
        });
        builder.setView(newSubjectLayout);
        builder.setPositiveButton(R.string.text_button_save, (dialog, which) -> {
            String title = subjectTitle.getText().toString();
            if (!title.isEmpty()) {
                saveSubject(title, colorPicked[0]);
            } else {
                Toast.makeText(getContext(), R.string.toast_message_title_empty, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.text_button_cancel, ((dialog, which) -> { }));
        AlertDialog alertDialog = builder.create();
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    private void showDialogEditSubject(Subject subject) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.dialog_edit_subject_dialog_title);
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_edit_subject, null);
        EditText editTitle = editLayout.findViewById(R.id.edit_title_dialog);
        editTitle.setText(subject.getTitle());
        editTitle.requestFocus(); // required for API 28+
        editTitle.setSelection(editTitle.getText().length());
        RadioGroup colorPicker = editLayout.findViewById(R.id.radio_group_color_picker);
        final int[] colorPicked = new int[1];
        colorPicked[0] = subject.getColor();
        colorPicker.setOnCheckedChangeListener((group, checkedId) -> {
            colorPicked[0] = group.findViewById(checkedId).getBackgroundTintList().getDefaultColor();
            editTitle.setTextColor(colorPicked[0]);
            editTitle.getBackground().setTint(colorPicked[0]);
        });
        // to select the color that the current subject contain
        String radioBtnTag = Integer.toHexString(colorPicked[0]).substring(2).toUpperCase();
        RadioButton radioBtnChecked = colorPicker.findViewWithTag(radioBtnTag);
        colorPicker.check(radioBtnChecked.getId());
        builder.setView(editLayout)
                .setPositiveButton(R.string.text_button_save, (dialog, which) -> {
                    subject.setTitle(editTitle.getText().toString());
                    subject.setColor(colorPicked[0]);
                    viewModel.reloadSubjectList();
                })
                .setNegativeButton(R.string.text_button_cancel, (dialog, which) -> {});

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            // scroll to the position of the color of the subject
            int x = radioBtnChecked.getLeft();
            int y = radioBtnChecked.getTop();
            editLayout.findViewById(R.id.horizontalScrollView).scrollTo(x, y);
        });
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public void showDialogDeleteSubject(int position) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Are you sure?")
                .setMessage("The subject will be deleted")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteSubject(position);
                    Toast.makeText(requireContext(), "Subject deleted", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void saveSubject(String title, int color) {
        Subject subject = new Subject(title, color);
        subjectList.add(subject);
        viewModel.reloadSubjectList();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.commitChanges();
    }
}