package com.example.onlinetaskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText EntryTitle, EntryContent;
    private TextView EntryDateTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        EntryTitle = findViewById(R.id.EntryTitle);
        EntryContent = findViewById(R.id.EntryContent);
        EntryDateTime = findViewById(R.id.EntryDateTime);

        EntryDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView BackButtonMain = findViewById(R.id.backButton);
        BackButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);}
        });

    }
    private void saveNote() {
        if (EntryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateNoteActivity.this, R.string.TitleEmpty, Toast.LENGTH_SHORT).show();
        }else if(EntryContent.getText().toString().trim().isEmpty()){
            Toast.makeText(CreateNoteActivity.this, R.string.ContentEmpty, Toast.LENGTH_SHORT).show();
        }
        Note note = new Note();
        note.setId();
        note.setTitle(EntryTitle.getText().toString());
        note.setContent(EntryContent.getText().toString());

    }

}