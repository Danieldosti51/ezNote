package com.danieldosti.eznote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.danieldosti.eznote.database.NoteEntity;
import com.danieldosti.eznote.ui.NoteAdapter;
import com.danieldosti.eznote.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    void fabClickHandler(){
        Intent editorIntent = new Intent(this, EditorActivity.class);
        startActivity(editorIntent);
    }

    private List<NoteEntity> mNotes = new ArrayList<>();
    private NoteAdapter mAdapter;
    private MainViewModel mViewModel;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();
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

        final Observer<List<NoteEntity>> observer = noteEntities -> {
            mNotes.clear();
            mNotes.addAll(noteEntities);

            if(mAdapter==null){
                mAdapter = new NoteAdapter(mNotes,this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        };

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.mNotes.observe(this, observer);
    }

    private void initRecyclerView() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean grid = preferences.getBoolean(getString(R.string.pref_grid_view),false);
        if(grid) {
            GridLayoutManager layoutManager = new GridLayoutManager(this,2);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        initTouchHelper();
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFor(newText);
                return true;
            }
        });
        return true;
    }

    private void searchFor(String newText) {
        mViewModel.searchFor(newText).observe(this,noteEntities -> {
            mNotes.clear();
            mNotes.addAll(noteEntities);

            if(mAdapter==null){
                mAdapter = new NoteAdapter(mNotes,this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTouchHelper() {
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mNotes, i, i + 1);

                        int order1 = mNotes.get(i).getPosition();
                        int order2 = mNotes.get(i + 1).getPosition();
                        mNotes.get(i).setPosition(order2);
                        mNotes.get(i + 1).setPosition(order1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mNotes, i, i - 1);

                        int order1 = mNotes.get(i).getPosition();
                        int order2 = mNotes.get(i - 1).getPosition();
                        mNotes.get(i).setPosition(order2);
                        mNotes.get(i - 1).setPosition(order1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                NoteEntity note = mNotes.get(pos);
                mViewModel.delete(note);
                mAdapter.notifyItemRemoved(pos);
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                mViewModel.update(mNotes);
            }
        });
    }
}
