package com.example.notesapp

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class createnote : AppCompatActivity() {
    private lateinit var mcreateTitleOfNote: EditText
    private lateinit var mcreateContentOfNote: EditText
    private lateinit var mSaveNote: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var noteId: String? = null // To hold the note ID for editing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createnote)

        mSaveNote = findViewById(R.id.savenote)
        mcreateContentOfNote = findViewById(R.id.createcontentofnote)
        mcreateTitleOfNote = findViewById(R.id.createtitleofnote)

        val toolbar: Toolbar = findViewById(R.id.toolbarofcreatenote)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Check if there is a NOTE_ID passed to this activity
        noteId = intent.getStringExtra("NOTE_ID")

        if (!noteId.isNullOrEmpty()) {
            // Load existing note details for editing
            loadNoteDetails(noteId!!)
        }

        mSaveNote.setOnClickListener {
            val title = mcreateTitleOfNote.text.toString()
            val content = mcreateContentOfNote.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(applicationContext, "Both fields are required", Toast.LENGTH_SHORT).show()
            } else {
                if (noteId != null) {
                    // Update the existing note
                    updateNote(noteId!!, title, content)
                } else {
                    // Create a new note
                    createNewNote(title, content)
                }
            }
        }
    }

    private fun loadNoteDetails(noteId: String) {
        val docRef = firebaseFirestore.collection("notes")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("myNotes")
            .document(noteId)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                mcreateTitleOfNote.setText(document.getString("title"))
                mcreateContentOfNote.setText(document.getString("content"))
            } else {
                Toast.makeText(this, "Note does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load note details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewNote(title: String, content: String) {
        val documentReference = firebaseFirestore.collection("notes")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("myNotes")
            .document() // Generate a new document ID

        val note = hashMapOf(
            "title" to title,
            "content" to content
        )

        documentReference.set(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note Created Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, notesactivity::class.java))
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Failed to Create Note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNote(noteId: String, title: String, content: String) {
        val note = hashMapOf(
            "title" to title,
            "content" to content
        )

        firebaseFirestore.collection("notes")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("myNotes")
            .document(noteId)
            .set(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, notesactivity::class.java))
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Failed to Update Note", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
