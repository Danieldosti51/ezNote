package com.danieldosti.eznote.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danieldosti.eznote.EditorActivity;
import com.danieldosti.eznote.R;
import com.danieldosti.eznote.database.NoteEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.danieldosti.eznote.utilities.Constants.NOTE_ID_KEY;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final List<NoteEntity> mNotes;
    private final Context mContext;

    public NoteAdapter(List<NoteEntity> mNotes, Context mContext) {
        this.mNotes = mNotes;
        this.mContext = mContext;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NoteEntity note = mNotes.get(position);
        holder.mTextView.setText(note.getText());
        holder.mTitleView.setText(note.getTitle());
        holder.mButton.setOnClickListener(v -> {
            Intent editorIntent = new Intent(mContext, EditorActivity.class);
            editorIntent.putExtra(NOTE_ID_KEY, note.getId());
            mContext.startActivity(editorIntent);
        });
    }

    @Override
    public long getItemId(int position) {
        return mNotes.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.note_text)
        TextView mTextView;

        @BindView(R.id.note_title)
        TextView mTitleView;

        @BindView(R.id.note_button)
        LinearLayout mButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
