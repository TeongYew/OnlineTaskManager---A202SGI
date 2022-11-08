package com.example.onlinetaskmanager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateNoteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText EntryTitle, EntryContent;
    private TextView EntryDateTime, DateButtonText, TimeButton;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        EntryTitle = findViewById(R.id.EntryTitle);
        EntryContent = findViewById(R.id.EntryContent);
        EntryDateTime = findViewById(R.id.EntryDateTime);
        DateButtonText = findViewById(R.id.DateButton);
        TimeButton = findViewById(R.id.TimeButton);

        //Displaying current date and time of note creation.
        EntryDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        //Button back to main menu. (need to add confirmation as well, "unsaved noted will be deleted")
        ImageView BackButtonMain = findViewById(R.id.backButton);
        BackButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);}
        });

        //Button to set deadline date
        TextView DateButtonMain = findViewById(R.id.DateButton);
        DateButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        //Button to set deadline time
        TextView TimeButtonMain = findViewById(R.id.TimeButton);
        TimeButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker();
            }
        });

        ImageView AddNoteButton = findViewById(R.id.saveButton);
        AddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateNote();
                saveNote();
            }
        });

    }

    //Date picker for deadline date.
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    //Time picker for deadline time.
    public void popTimePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time = selectedHour + ":" + selectedMinute;
                TimeButton.setText(time);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,hour,minute,true);

        timePickerDialog.setTitle("Select Deadline Time");
        timePickerDialog.show();
    }

    //Validate that note title and note content are filled.
    private void validateNote() {
        if (EntryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateNoteActivity.this, R.string.TitleEmpty, Toast.LENGTH_SHORT).show();
        }else if(EntryContent.getText().toString().trim().isEmpty()){
            Toast.makeText(CreateNoteActivity.this, R.string.ContentEmpty, Toast.LENGTH_SHORT).show();
        }
    }

    //Add note to hashmap to upload to firestore. FIX HERE <----------------
    public void saveNote() {
        Map<String, Object> note = new HashMap<>();
        note.put("note_content", EntryContent.toString());
        note.put("note_title", EntryTitle.toString());

        db.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int month, int i2) {
        month =  month + 1;
        String date = i2 + "/" + month + "/" + i;
        DateButtonText.setText(date);
    }
}