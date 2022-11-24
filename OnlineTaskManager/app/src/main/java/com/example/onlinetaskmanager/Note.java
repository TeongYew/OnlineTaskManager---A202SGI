package com.example.onlinetaskmanager;

public class Note {

    String note_title, note_content;

    public Note(){}

    public Note(String note_title, String note_content) {
        this.note_title = note_title;
        this.note_content = note_content;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }
}
