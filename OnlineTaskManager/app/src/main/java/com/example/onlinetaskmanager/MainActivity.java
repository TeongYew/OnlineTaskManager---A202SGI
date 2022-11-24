package com.example.onlinetaskmanager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Note> noteArrayList;
    MyAdapter myAdapter;
    String testUserId2 = "1112";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView AddNoteButtonMain = findViewById(R.id.AddNoteButton);
        AddNoteButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateNoteActivity.class);
                view.getContext().startActivity(intent);}
        });

        FloatingActionButton profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
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