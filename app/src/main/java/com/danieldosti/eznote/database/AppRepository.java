package com.danieldosti.eznote.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<NoteEntity>> mNotes;
    private AppDatabase mDb;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public static AppRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AppRepository(context);
        }
        return ourInstance;
    }

    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
        mNotes = getAllNotes();
    }

    private LiveData<List<NoteEntity>> getAllNotes(){
        return mDb.noteDao().getAllNotes();
    }

    public NoteEntity getNoteByID(int ID){
        return mDb.noteDao().getNoteByID(ID);
    }

    public void insertNote(NoteEntity note) {
        executor.execute(() -> mDb.noteDao().InsertNote(note));
    }

    public void update(List<NoteEntity> noteEntities){
        executor.execute(() -> mDb.noteDao().update(noteEntities));
    }

    public void delete(NoteEntity note) {
        executor.execute(() -> mDb.noteDao().deleteNote(note));
    }

    public int getCount() {
        try {
            Callable<Integer> task = () -> mDb.noteDao().getCount();
            Future<Integer> result = executor.submit(task);
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getMaxPos() {
        try {
            Callable<Integer> task = () -> mDb.noteDao().getMaxPos();
            Future<Integer> result = executor.submit(task);
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public LiveData<List<NoteEntity>> searchFor(String query) {
        return mDb.noteDao().searchFor(query);
    }
}
