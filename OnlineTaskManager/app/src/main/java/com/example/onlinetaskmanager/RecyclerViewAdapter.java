package com.example.onlinetaskmanager;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter {



    static class NoteTileHolder extends RecyclerView.ViewHolder {

        List<String> NoteIdList = new ArrayList<String>();
        TextView TileTitle, TileContent, TileDeadline ;
        TextView IdText;

        NoteTileHolder(@NonNull View itemView) {
            super(itemView);
            TileTitle = itemView.findViewById(R.id.TileTitle);
            TileContent = itemView.findViewById(R.id.TileContent);
            TileDeadline = itemView.findViewById(R.id.TileDeadline);
        }

        void setNote(){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("notes_users")
                    .whereEqualTo("user_id", "1111")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    NoteIdList.add(document.getString("notes_id"));
                                }
                            }
                        }
                    });
        }


    }
}
