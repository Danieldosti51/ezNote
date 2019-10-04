package com.danieldosti.eznote.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes")
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int position;
    private String title;
    private String text;
    private Date date;
    private Boolean edited;

    @Ignore
    public NoteEntity() {
    }

    @Ignore
    public NoteEntity(int position, String title, String text, Date date) {
        this.position = position;
        this.title = title;
        this.text = text;
        this.date = date;
        this.edited = false;
    }

    public NoteEntity(int id, int position, String title, String text, Date date) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.text = text;
        this.date = date;
        this.edited = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", position=" + position +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
