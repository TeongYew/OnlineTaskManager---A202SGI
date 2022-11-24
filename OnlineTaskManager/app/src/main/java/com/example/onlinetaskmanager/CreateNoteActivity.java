package com.example.onlinetaskmanager;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.TestLooperManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class CreateNoteActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String dummyuserID = "1111"; //dummy user id <--- Need to replace with get instance get current userid.
    String testnoteID = "0002"; //dummy note id <--- Need to be autogenerated
    private EditText EntryTitle, EntryContent;
    private TextView  DateButton, TimeButton;
    int hour, minute;
    private ImageView backBtn;
    String testNoteID2 = "odpXTeyElv2iLpF95nCf";

    FloatingActionButton calendarFAB;
    Button resetBtn;
    String realNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Calendar dueDateTime = Calendar.getInstance();
        EntryTitle = findViewById(R.id.EntryTitle);
        EntryContent = findViewById(R.id.EntryContent);
        //EntryDateTime = findViewById(R.id.EntryDateTime);
        DateButton = findViewById(R.id.DateButton);
        TimeButton = findViewById(R.id.TimeButton);
        backBtn = findViewById(R.id.backButton);
        calendarFAB = findViewById(R.id.calendarFAB);
        resetBtn = findViewById(R.id.resetBtn);

        //Displaying current date and time of note creation.
//        EntryDateTime.setText(
//                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
//                        .format(new Date())
//        );

        //getNoteWithNoteId();
        //getAllNotesWithUserId();

        //Button back to main menu. (need to add confirmation as well, "unsaved noted will be deleted")
        ImageView BackButtonMain = findViewById(R.id.backButton);
        BackButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);}
        });

        //Button to set deadline date
        DateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        String date = year + "/" + month + "/" + day;
                        DateButton.setText(date);

                        dueDateTime.set(Calendar.YEAR, year);
                        dueDateTime.set(Calendar.MONTH, month);
                        dueDateTime.set(Calendar.DAY_OF_MONTH, day);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateNoteActivity.this,
                        onDateSetListener,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle("Select Deadline Date");
                datePickerDialog.show();
            }
        });

        //Button to set deadline time
        TimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = selectedHour + ":" + selectedMinute;
                        TimeButton.setText(time);

                        dueDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        dueDateTime.set(Calendar.MINUTE, selectedMinute);
                        dueDateTime.set(Calendar.SECOND, 0);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNoteActivity.this, onTimeSetListener,hour,minute,true);

                timePickerDialog.setTitle("Select Deadline Time");
                timePickerDialog.show();
            }
        });

        ImageView AddNoteButton = findViewById(R.id.saveButton);
        AddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validate that note title and note content are filled.
                if (EntryTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateNoteActivity.this, R.string.TitleEmpty, Toast.LENGTH_SHORT).show();
                }else if(EntryContent.getText().toString().trim().isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, R.string.ContentEmpty, Toast.LENGTH_SHORT).show();
                }else{

                    //check if it's a new note using the intent that is sent


                    //addNote(dueDateTime);

                    //if dueDateTime is already set previously but not changed
                    //we need to set dueDateTime again using the current date and time textview/button
                    if (!DateButton.getText().toString().equals("Date") && !TimeButton.getText().toString().equals("Time")){

                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                        try {

                            Date dateTime = format.parse(DateButton.getText().toString() + " " + TimeButton.getText().toString());
                            Log.d(TAG, "onClick: dis is parsed datetime: " + dateTime.toString());
                            dueDateTime.setTime(dateTime);
                            dueDateTime.add(Calendar.MONTH, 1);
                            updateNote(dueDateTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }


                    //if datebutton and timebutton is not its default value, that means it has been set
                    //so we can go into the setReminder function
                    if (!DateButton.getText().toString().equals("Date") && !TimeButton.getText().toString().equals("Time")){
                        Log.d(TAG, "onClick: got into setReminder condition check");
                        //setReminder(dueDateTime);
                    }
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);

                }

            }
        });

        //set listener for the button to go back to main menu
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to main menu
                Intent intent = new Intent(CreateNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        calendarFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCalendar(dueDateTime);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateButton.setText("Date");
                TimeButton.setText("Time");
                //testing delete note method
                //deleteNote("44AG5nNic6H0c3PWoSKh");
            }
        });


    }


    //Add note to hashmap to upload to firestore. link to user id and insert into user_note collection.
    public void addNote(Calendar c) {

        Map<String, Object> note = new HashMap<>();
        note.put("note_content", EntryContent.getText().toString());
        note.put("note_title", EntryTitle.getText().toString());
        note.put("note_id", testnoteID);

        //check if dueDateTime has been set
        //only add due_date_time if it has been set
        if (!DateButton.getText().toString().equals("Date") && !TimeButton.getText().toString().equals("Time")){
            Date dueDateTimeStamp = c.getTime();
            note.put("due_date_time", dueDateTimeStamp);
        }

        //first create a new note in the notes collection in firestore
        db.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(CreateNoteActivity.this, R.string.CreationSuccess, Toast.LENGTH_SHORT).show();

                        //once the note is created, get the firestore generated id
                        realNoteId = documentReference.getId();
                        Log.d(TAG, "saveNote: got to the update:" + realNoteId);

                        //update the newly created note's note_id with the firestore generated id
                        db.collection("notes")
                                .document(realNoteId)
                                .update("note_id", realNoteId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                                        Map<String, Object> notes_users = new HashMap<>();
                                        notes_users.put("notes_id", realNoteId); //generate user ID then add here to add to firestore.
                                        notes_users.put("user_id", dummyuserID); //find a way to pull user id for now can use dummy for testing.

                                        //create a new notes_user using the user_id and the note_id(firestore generated id)
                                        db.collection("notes_users")
                                                .add(notes_users)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding notes_users document", e);
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating note_id document", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding notes document", e);
                    }
                });

    }

    private void updateNote(Calendar c){

        Map<String, Object> note = new HashMap<>();
        note.put("note_content", EntryContent.getText().toString());
        note.put("note_title", EntryTitle.getText().toString());

        //check if dueDateTime has been set
        if (!DateButton.getText().toString().equals("Date") && !TimeButton.getText().toString().equals("Time")){
            Date dueDateTimeStamp = c.getTime();
            note.put("due_date_time", dueDateTimeStamp);
        }

        db.collection("notes").document(testNoteID2)
                .update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }


    private void getNoteWithNoteId(){

        db.collection("notes").document(testNoteID2)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "DocumentSnapshot data2: " + document.get("note_title"));
                        Log.d(TAG, "DocumentSnapshot data3: " + document.get("due_date_time"));
                        EntryTitle.setText(document.get("note_title").toString());
                        EntryContent.setText(document.get("note_content").toString());
                        Date dueDateTimestamp = document.getDate("due_date_time");
                        Log.d(TAG, "onComplete: dis is note due date time: " + dueDateTimestamp.toString());
                        Calendar dueDateTime = new GregorianCalendar();
                        dueDateTime.setTime(dueDateTimestamp);
                        int year,month,day,hour,minute;
                        String date;
                        String time;

                        year = dueDateTime.get(Calendar.YEAR);
                        month = dueDateTime.get(Calendar.MONTH);
                        day = dueDateTime.get(Calendar.DAY_OF_MONTH);
                        hour = dueDateTime.get(Calendar.HOUR_OF_DAY);
                        minute = dueDateTime.get(Calendar.MINUTE);

                        date = year + "/" + month + "/" + day;
                        time = hour + ":" + minute;
                        Log.d(TAG, "onComplete: dis is date and time: " + date + " " + time);

                        DateButton.setText(date);
                        TimeButton.setText(time);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }



    private void setReminder(Calendar c){

        Log.d(TAG, "setReminder: " + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR)+ c.get(Calendar.MINUTE));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationPublisher.class);
        intent.putExtra("title", EntryTitle.getText());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (!c.before(Calendar.getInstance())) {
            Log.d(TAG, "setReminder: got into not before current datetime");
            Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(), pendingIntent);
        }

    }

    private void setCalendar(Calendar c){

        Log.d(TAG, "setCalendar: " + c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR)+ c.get(Calendar.MINUTE));

        if(!EntryTitle.getText().toString().isEmpty()){

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, EntryTitle.getText().toString());



            try{
                startActivity(intent);
            }catch(Exception e){
                Toast.makeText(this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }

        }
        else{
            Toast.makeText(this, "Please make sure all the fields are filled in", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote(String noteId){

        db.collection("notes").document(noteId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

}