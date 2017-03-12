package com.francescocipolla.onionnotes.adapters;

import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.colorpicker.ColorPickerSwatch;
import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.activities.MainActivity;
import com.francescocipolla.onionnotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ciccio on 20/02/2017.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteVH> {

    private static DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
    private ArrayList<Note> dataSet = new ArrayList<>();
    private int position;


    public void addNote(Note note) {
        dataSet.add(0, note);
        notifyItemInserted(0);
    }

    public void editNote(Note note, int position) {
        dataSet.set(position, note);
        notifyItemChanged(position);
    }

    public void deleteNote(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<Note> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<Note> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public NoteAdapter.NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater instantiates a layout XML file into its corresponding View objects.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteVH(view);
    }

    @Override
    public void onBindViewHolder(NoteVH holder, int position) {
        Note lastNote = dataSet.get(position);
        holder.title.setText(lastNote.getTitle());
        holder.body.setText(lastNote.getBody());
        holder.lastUpdate.setText(lastNote.getLastUpdateDate());
        holder.expireDate.setText(lastNote.getExpireDate());
        int visibility = (lastNote.isBookmarked()) ? View.VISIBLE : View.INVISIBLE;
        holder.itemView.findViewById(R.id.item_bookmark_id).setVisibility(visibility);
        holder.itemView.setBackgroundColor(lastNote.getNoteColor());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setNoteColor(int color) {


    }
    class NoteVH extends RecyclerView.ViewHolder {
        TextView title, body, lastUpdate, expireDate;
        ActionMode mActionMode;
        ActionMode.Callback mActionModeCallback;

        NoteVH(final View itemView) {
            super(itemView);
            // Connect the XML components and the NoteView java attributes
            title = (TextView) itemView.findViewById(R.id.item_title_id);
            body = (TextView) itemView.findViewById(R.id.item_body_id);
            lastUpdate = (TextView) itemView.findViewById(R.id.item_last_update_id);
            expireDate = (TextView) itemView.findViewById(R.id.item_expire_date_id);
            lastUpdate.setText(dateFormat.format(new Date()));

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mActionMode = ((MainActivity) v.getContext()).actionMode;
                    mActionModeCallback = ((MainActivity) v.getContext()).mActionCallback;
                    if (mActionMode != null) {
                        return false;
                    }
                    setPosition(getAdapterPosition());
                    mActionMode = (((MainActivity) v.getContext())).startSupportActionMode(mActionModeCallback);
                    return true;
                }
            });

        }
    }
}
