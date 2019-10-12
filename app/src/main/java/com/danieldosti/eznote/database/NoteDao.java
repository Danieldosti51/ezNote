package com.danieldosti.eznote.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNote(NoteEntity note);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<NoteEntity> noteEntities);

    @Delete
    void deleteNote(NoteEntity note);

    @Query("SELECT * FROM notes WHERE id = :id")
    NoteEntity getNoteByID(int id);

    @Query("SELECT MAX(position) from notes")
    int getMaxPos();

    @Query("SELECT * FROM notes ORDER BY position DESC")
    LiveData<List<NoteEntity>> getAllNotes();

    @Query("SELECT * FROM notes WHERE title LIKE :s OR text LIKE :s ORDER BY position DESC")
    LiveData<List<NoteEntity>> searchFor(String s);

}
