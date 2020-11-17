package com.themarto.etudetask.viewmodel;

import com.themarto.etudetask.SubjectRepository;
import com.themarto.etudetask.Util;
import com.themarto.etudetask.models.Section;
import com.themarto.etudetask.models.Subject;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private SubjectRepository mRepository;
    private MutableLiveData<List<Subject>> subjects;
    private MutableLiveData<Subject> selectedSubject = new MutableLiveData<>();
    private MutableLiveData<Section> selectedSection = new MutableLiveData<>();
    private MutableLiveData<Task> selectedTask = new MutableLiveData<>();

    public SharedViewModel() {
        mRepository = new SubjectRepository();
        subjects = new MutableLiveData<>();
        loadSubjects();
    }

    public LiveData<List<Subject>> getAllSubjects () {
        return subjects;
    }

    public void setStartSubject(int position) {
        selectedSubject.setValue(subjects.getValue().get(position));
    }

    public void loadSubjects(){
        subjects = mRepository.getAllSubjects();
        if(subjects.getValue().isEmpty()) {
            mRepository.addSubject(new Subject("Default")); // TODO: string
            selectSubject(0);
        }
    }

    public void selectSubject (int position) {
        selectedSubject.setValue(subjects.getValue().get(position));
    }

    public LiveData<Subject> getSelectedSubject () {
        return selectedSubject;
    }

    public void selectSection (int position) {
        Section section = selectedSubject.getValue().getSectionList().get(position);
        selectedSection.setValue(section);
    }

    public LiveData<Section> getSelectedSection () {
        return  selectedSection;
    }

    public void selectTask (int position) {
        Task task = selectedSection.getValue().getTaskList().get(position);
        selectedTask.setValue(task);
    }

    public LiveData<Task> getSelectedTask () {
        return selectedTask;
    }

    // CRUD actions
    public void addSubject(Subject newSubject) {
        mRepository.addSubject(newSubject);
        selectSubject(subjects.getValue().size() - 1);
    }

    /**
     * Change the title of the selected subject
     * @param title new title for the subject
     */
    public void changeSubjectTitle (String title) {
        Subject subject = mRepository.changeSubjectTitle(selectedSubject.getValue(), title);
        selectedSubject.setValue(subject);
    }

    /**
     * Delete the selected subject
     */
    public boolean deleteSubject () {
        if (subjects.getValue().size() > 1) {
            mRepository.deleteSubject(selectedSubject.getValue());
            selectedSubject.setValue(subjects.getValue().get(0));
            return true;
        }
        return false;
    }

    /**
     * Add a new Section to the selected subject
     * @param section new section to be added
     */
    public void addSection (Section section) {
        Subject subject = mRepository.addSection(selectedSubject.getValue(), section);
        selectedSubject.setValue(subject);
    }

    /**
     * Change the title of the selected section
     * @param title new title of the section
     */
    public void changeSectionTitle (String title) {
        Section section = mRepository.changeSectionTitle(selectedSection.getValue(), title);
        selectedSection.setValue(section);
    }

    /**
     * Delete the selected section
     */
    public void deleteSection () {
        mRepository.deleteSection(selectedSection.getValue());
    }

    /**
     * Add a new task to the selected section
     * @param task new task to be added
     */
    public void addTask (Task task) {
        Section section = mRepository.addTask(selectedSection.getValue(), task);
        selectedSection.setValue(section);
    }

    /**
     * Change the task title of the selected task
     * @param title new title of the task
     */
    public void changeTaskTitle (String title) {
        Task task = mRepository.changeTaskTitle(selectedTask.getValue(), title);
        selectedTask.setValue(task);
    }

    /**
     * Delete selected task
     */
    public void deleteTask () {
        mRepository.deleteTask(selectedTask.getValue());
    }

    /**
     * Delete task in the position received
     * @param position the position of the task to be deleted
     */
    public void deleteTask (int position) {
        Section section = mRepository.deleteTask(selectedSection.getValue(), position);
        selectedSection.setValue(section);
    }

}
