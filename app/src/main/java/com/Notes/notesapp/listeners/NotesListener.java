package com.Notes.notesapp.listeners;

import com.Notes.notesapp.entities.Note;

public interface NotesListener {

    void onNoteClicked(Note note,int position);
}
