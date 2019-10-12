package com.danieldosti.eznote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.danieldosti.eznote.database.AppRepository;
import com.danieldosti.eznote.database.NoteEntity;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public LiveData<List<NoteEntity>> mNotes;
    private AppRepository mRepo;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepo = AppRepository.getInstance(application.getApplicationContext());
        mNotes = mRepo.mNotes;
    }

    public void update(List<NoteEntity> noteEntities) {
        mRepo.update(noteEntities);
    }

    public void delete(NoteEntity note) {
        mRepo.delete(note);
    }

    public LiveData<List<NoteEntity>> searchFor(String query) {
        query = "%"+query+"%";  //query string wrapped by % wildcard for noteDAO
        return mRepo.searchFor(query);
    }
}
