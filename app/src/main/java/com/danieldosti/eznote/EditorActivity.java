package com.danieldosti.eznote;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.danieldosti.eznote.database.NoteEntity;
import com.danieldosti.eznote.viewmodel.EditorViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.danieldosti.eznote.utilities.Constants.NOTE_ID_KEY;

public class EditorActivity extends AppCompatActivity {

    @BindView(R.id.note_text)
    TextView noteText;

    @BindView(R.id.note_title)
    TextView noteTitle;

    @BindView(R.id.note_date)
    TextView noteDate;

    private EditorViewModel mViewModel;
    private boolean mNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        initViewModel();
    }

    private void initTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString(getString(R.string.pref_theme),"Deep Purple");
        if(theme.equals("Deep Purple")) {
            setTheme(R.style.AppThemePurple_NoActionBar);
        } else if (theme.equals("Teal")) {
            setTheme(R.style.AppThemeTeal_NoActionBar);
        } else {
            setTheme(R.style.AppThemeIndigo_NoActionBar);
        }
    }

    private void initViewModel() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.getBoolean(getString(R.string.pref_date),true);
        mViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);

        mViewModel.mLiveNote.observe(this, noteEntity -> {
            if (noteEntity != null) {
                noteText.setText(noteEntity.getText());
                noteTitle.setText(noteEntity.getTitle());
                if(preferences.getBoolean(getString(R.string.pref_date),true)){
                    noteDate.setText(mViewModel.formatDate(noteEntity.getDate(), noteEntity.getEdited()));
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("New Note");
            mNewNote = true;
        } else {
            setTitle("Edit Note");
            int noteID = extras.getInt(NOTE_ID_KEY);
            mViewModel.loadData(noteID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(!mNewNote){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            saveAndReturn();
            return true;
        } else if(item.getItemId() == R.id.action_delete){
            NoteEntity note = mViewModel.deleteNote();
            Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        int result = mViewModel.saveNote(noteText.getText().toString(), noteTitle.getText().toString());
        if(result == 1) {
            Toast.makeText(this, "Empty note, discarded", Toast.LENGTH_SHORT).show();
        } else if(result == 2) {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
