package com.themarto.etudetask;

import com.themarto.etudetask.models.Chapter;
import com.themarto.etudetask.models.Signature;
import com.themarto.etudetask.models.Task;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class SignatureRepository {

    private Realm realm;

    private MutableLiveData<List<Signature>> allSignatures;

    public SignatureRepository() {
        realm = Realm.getDefaultInstance();
        allSignatures = new MutableLiveData<>();
        RealmResults<Signature> signatures = realm.where(Signature.class).findAll();
        allSignatures.setValue(signatures);
    }

    public MutableLiveData<List<Signature>> getAllSignatures() {
        return allSignatures;
    }

    public void addSignature(Signature signature) {
        realm.beginTransaction();
        realm.copyToRealm(signature);
        realm.commitTransaction();
    }

    public Signature changeSignatureTitle(Signature signature, String nTitle) {
        realm.beginTransaction();
        signature.setTitle(nTitle);
        realm.commitTransaction();
        return signature;
    }

    public void deleteSignature(Signature signature) {
        realm.beginTransaction();
        signature.deleteFromRealm();
        realm.commitTransaction();
    }

    public Signature addChapter(Signature signature, Chapter nChapter) {
        realm.beginTransaction();
        signature.getChapterList().add(nChapter);
        realm.commitTransaction();
        return signature;
    }

    public Chapter changeChapterTitle(Chapter chapter, String nTitle) {
        realm.beginTransaction();
        chapter.setTitle(nTitle);
        realm.commitTransaction();
        return chapter;
    }

    public void deleteChapter(Chapter chapter) {
        realm.beginTransaction();
        chapter.deleteFromRealm();
        realm.commitTransaction();
    }

    public Chapter addTask(Chapter chapter, Task nTask) {
        realm.beginTransaction();
        chapter.getTaskList().add(nTask);
        realm.commitTransaction();
        return chapter;
    }

    public Task changeTaskTitle(Task task, String nTitle) {
        realm.beginTransaction();
        task.setTitle(nTitle);
        realm.commitTransaction();
        return task;
    }

    public void deleteTask(Task task) {
        realm.beginTransaction();
        task.deleteFromRealm();
        realm.commitTransaction();
    }

}
