package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var editFab: FloatingActionButton
    private lateinit var deleteFab: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        titleTextView = findViewById(R.id.note_title)
        contentTextView = findViewById(R.id.note_content)
        editFab = findViewById(R.id.edit_fab)
        deleteFab = findViewById(R.id.delete_fab)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get the note ID and details from the intent
        noteId = intent.getStringExtra("NOTE_ID") ?: ""
        titleTextView.text = intent.getStringExtra("NOTE_TITLE")
        contentTextView.text = intent.getStringExtra("NOTE_CONTENT")

        // Set up edit button click listener
        editFab.setOnClickListener {
            val intent = Intent(this, createnote::class.java)
            intent.putExtra("NOTE_ID", noteId)
            intent.putExtra("NOTE_TITLE", titleTextView.text)
            intent.putExtra("NOTE_CONTENT", contentTextView.text)
            startActivity(intent)
        }

        // Set up delete button click listener
        deleteFab.setOnClickListener {
            deleteNote()
        }
    }

    private fun deleteNote() {
        firestore.collection("notes")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("myNotes")
            .document(noteId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show()
                // Redirect to notesactivity
                val intent = Intent(this, notesactivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Delete Note", Toast.LENGTH_SHORT).show()
            }
    }
}
