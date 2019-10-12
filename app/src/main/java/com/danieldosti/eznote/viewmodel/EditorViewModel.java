package com.danieldosti.eznote.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.danieldosti.eznote.database.AppRepository;
import com.danieldosti.eznote.database.NoteEntity;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EditorViewModel extends AndroidViewModel {

    public MutableLiveData<NoteEntity> mLiveNote = new MutableLiveData<>();
    private AppRepository mRepo;
    private Executor executor = Executors.newSingleThreadExecutor();

    public EditorViewModel(@NonNull Application application) {
        super(application);
        mRepo = AppRepository.getInstance(application.getApplicationContext());
    }

    public void loadData(int noteID) {
        executor.execute(() -> {
            NoteEntity note = mRepo.getNoteByID(noteID);
            mLiveNote.postValue(note);
        });
    }

    public NoteEntity deleteNote() {
        NoteEntity note = mLiveNote.getValue();
        mRepo.delete(mLiveNote.getValue());
        return note;
    }

    public boolean saveNote(String text, String title) {
        NoteEntity note = mLiveNote.getValue();
        if (note != null) {
            if((note.getText().equals(text)) && (note.getTitle().equals(title))){
                return false;   //no changes
            }
            note.setText(text);
            note.setTitle(title);
            note.setDate(new Date());
            note.setEdited(true);
        } else {
            if(TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(title.trim())){
                return true;    //empty note
            }
            note = new NoteEntity(mRepo.getMaxPos()+1, title.trim(), text.trim(), new Date());
        }
        mRepo.insertNote(note);
        return false;
    }

    public String formatDate(Date date, boolean edited) {
        String result;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date);
        int noteYear = calendar.get(Calendar.YEAR);
        int noteDay = calendar.get(Calendar.DAY_OF_YEAR);
        if(noteDay == currentDay && currentYear == noteYear){
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            result = dateFormat.format(date);
        } else if(noteDay+1 == currentDay && currentYear == noteYear) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            result = "Yesterday, " + dateFormat.format(date);
        } else if(currentYear == noteYear) {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            result = dateFormat.format(date);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            result = dateFormat.format(date);
        }
        if(edited){
            result = "Edited " + result;
        }
        return result;
    }
}
