package com.themarto.etudetask;

import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.models.Task;

import java.util.ArrayList;
import java.util.List;

public class Util {

    static List<Task> taskListExample = new ArrayList<Task>(){{
        add((new Task("Task 1", "none", false)));
        add(new Task("Task 1", "none", false));
        add(new Task("Task 1", "none", false));
        add(new Task("Task 1", "none", false));
        add(new Task("Task 2", "none", false));
        add(new Task("Task 2", "none", false));
        add(new Task("Task 2", "none", false));
        add(new Task("Task 2", "none", false));
        add(new Task("Task 3", "none", false));
        add(new Task("Task 3", "none", false));
        add(new Task("Task 3", "none", false));
        add(new Task("Task 3", "none", false));
        add(new Task("Task 4", "none", true));
        add(new Task("Task 4", "none", true));
        add(new Task("Task 4", "none", true));
        add(new Task("Task 4", "none", true));
        add(new Task("Task 5", "none", true));
    }};

    static List<Chapter> chapterListExample = new ArrayList<Chapter>(){
        {
            add(new Chapter("Chapter 0", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 1", taskListExample));
            add(new Chapter("Chapter 9", taskListExample));
        }
    };

    static List<Signature> signatureList = new ArrayList<Signature>(){{
       add(new Signature("Stats", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Calculus", chapterListExample));
       add(new Signature("Algebra", chapterListExample));
    }};

    public static List<Chapter> getChapterListEx(){
        return chapterListExample;
    }

    public static List<Task> getTaskListExample () { return  taskListExample; }

    public static List<Signature> getSignatureListEx () { return signatureList; }

    public interface MyListener {
        void onItemClick(int position);
    }
}