package com.example.onlinetaskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewholder> {

    Context context;
    ArrayList<Note> NoteArrayList;

    public MyAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        NoteArrayList = noteArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.note_container,parent,false);

        return new MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewholder holder, int position) {

        Note note = NoteArrayList.get(position);

        holder.TileContent.setText(note.note_content);
        holder.TileTitle.setText(note.note_title);

    }

    @Override
    public int getItemCount() {
        return NoteArrayList.size();
    }
    public static class MyViewholder extends RecyclerView.ViewHolder{

        TextView TileTitle, TileContent;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            TileTitle = itemView.findViewById(R.id.TileTitle);
            TileContent = itemView.findViewById(R.id.TileContent);
        }
    }
}
