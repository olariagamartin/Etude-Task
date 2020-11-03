package com.themarto.etudetask;

import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.models.Task;

import java.util.ArrayList;
import java.util.List;

public class Util {

    static List<Chapter> chapterListExample = new ArrayList<Chapter>(){
        {
            add(new Chapter("Chapter 0", null));
            add(new Chapter("Chapter 1", null));
            add(new Chapter("Chapter 1", null));
            add(new Chapter("Chapter 2", null));
            add(new Chapter("Chapter 2", null));
            add(new Chapter("Chapter 3", null));
            add(new Chapter("Chapter 3", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 5", null));
            add(new Chapter("Chapter 9", null));
        }
    };

    static List<Task> taskListExample = new ArrayList<Task>(){{
       add(new Task("Task 1", "none", false));
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

    static List<Signature> signatureList = new ArrayList<Signature>(){{
       add(new Signature("Stats", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Calculus", null));
       add(new Signature("Algebra", null));
    }};

    public static List<Chapter> getChapterListEx(){
        return chapterListExample;
    }

    public static List<Task> getTaskListExample () { return  taskListExample; }

    public static List<Signature> getSignatureListEx () { return signatureList; }
}