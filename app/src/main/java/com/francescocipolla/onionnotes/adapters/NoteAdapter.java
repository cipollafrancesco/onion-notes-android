package com.francescocipolla.onionnotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.francescocipolla.onionnotes.R;
import com.francescocipolla.onionnotes.models.Note;

import java.util.ArrayList;

/**
 * Created by Ciccio on 20/02/2017.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteVH> {

    private ArrayList<Note> dataSet = new ArrayList<>();

    public void addNote(Note note) {
        dataSet.add(0, note);
        notifyItemInserted(0);
    }

    public void editNote(Note note, int position) {
        dataSet.set(position, note);
        notifyItemChanged(position);
    }

    public ArrayList<Note> getDataSet() {
        return dataSet;
    }

    @Override
    public NoteAdapter.NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater instantiates a layout XML file into its corresponding View objects.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,parent,false);
        return new NoteVH(view);
    }

    @Override
    public void onBindViewHolder(NoteVH holder, int position) {
        Note lastNote = dataSet.get(position);
        holder.title.setText(lastNote.getTitle());
        holder.body.setText(lastNote.getBody());
        holder.lastUpdate.setText(lastNote.getLastUpdateDate().toString());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class NoteVH extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        TextView lastUpdate;

        public NoteVH(View itemView) {
            super(itemView);
            // match between the XML components and the NoteView java attributes
            title = (TextView) itemView.findViewById(R.id.item_title_id);
            body = (TextView) itemView.findViewById(R.id.item_body_id);
            lastUpdate = (TextView) itemView.findViewById(R.id.item_last_update_id);

        }
    }
}
