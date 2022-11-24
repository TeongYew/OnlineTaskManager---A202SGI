package com.example.onlinetaskmanager;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewholder> {

    Context context;
    ArrayList<Note> NoteArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String testUserId2 = "1112";

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
        db.collection("notes_users")
                .whereEqualTo("user_id", testUserId2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<String> notesIdList = new ArrayList<String>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " => " + document.get("notes_id"));
                                notesIdList.add(document.get("notes_id").toString());
                            }

                            //loop through the arraylist that is holding all the notes_id of the user and get each note
                            for(int i=0; i<notesIdList.size(); i++) {

                                Log.d(TAG, "onComplete: " + i + notesIdList.get(i));

                                db.collection("notes")
                                        .whereEqualTo("note_id", notesIdList.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Log.d(TAG, document.getId() + " => " + document.get("note_title"));
                                                        Log.d(TAG, document.getId() + " => " + document.get("note_content"));
                                                        Note note = new Note(document.get("note_title").toString(),document.get("note_content").toString(), document.get("note_id").toString());
                                                        NoteArrayList.add(note);
                                                    }
                                                    for (int i =0; i<NoteArrayList.size();i++){
                                                        holder.TileTitle.setText(NoteArrayList.get(i).getNote_title());
                                                        holder.TileId.setText(NoteArrayList.get(i).getNote_id());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        Note note = NoteArrayList.get(position);

        holder.TileContent.setText(note.note_content);
        holder.TileTitle.setText(note.note_title);

    }

    @Override
    public int getItemCount() {
        return NoteArrayList.size();
    }
    public static class MyViewholder extends RecyclerView.ViewHolder{

        TextView TileTitle, TileContent, TileId;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            TileTitle = itemView.findViewById(R.id.TileTitle);
            TileContent = itemView.findViewById(R.id.TileContent);
            TileId = itemView.findViewById(R.id.TileId);
        }
    }

    private void getAllNotesWithUserId(){
        db.collection("notes_users")
                .whereEqualTo("user_id", testUserId2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<String> notesIdList = new ArrayList<String>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " => " + document.get("notes_id"));
                                notesIdList.add(document.get("notes_id").toString());
                            }

                            //loop through the arraylist that is holding all the notes_id of the user and get each note
                            for(int i=0; i<notesIdList.size(); i++) {

                                Log.d(TAG, "onComplete: " + i + notesIdList.get(i));

                                db.collection("notes")
                                        .whereEqualTo("note_id", notesIdList.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        Log.d(TAG, document.getId() + " => " + document.get("note_title"));
                                                        Log.d(TAG, document.getId() + " => " + document.get("note_content"));



                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
