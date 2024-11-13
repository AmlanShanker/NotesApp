package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        titleTextView = findViewById(R.id.note_title)
        contentTextView = findViewById(R.id.note_content)
        val editFab: FloatingActionButton = findViewById(R.id.edit_fab)
        val deleteFab: FloatingActionButton = findViewById(R.id.delete_fab)
        val backArrow = findViewById<ImageView>(R.id.back_arrow)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        noteId = intent.getStringExtra("NOTE_ID") ?: ""
        titleTextView.text = intent.getStringExtra("NOTE_TITLE")
        contentTextView.text = intent.getStringExtra("NOTE_CONTENT")


        val toolbar: Toolbar = findViewById(R.id.toolbar_note_detail)
        setSupportActionBar(toolbar)


        backArrow.setOnClickListener {
            navigateBackToNotesActivity()
        }

        editFab.setOnClickListener {
            val intent = Intent(this, createnote::class.java)
            intent.putExtra("NOTE_ID", noteId)
            intent.putExtra("NOTE_TITLE", titleTextView.text)
            intent.putExtra("NOTE_CONTENT", contentTextView.text)
            startActivity(intent)
        }

        deleteFab.setOnClickListener { confirmDeleteNote() }
    }

    private fun confirmDeleteNote() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ -> deleteNote() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteNote() {
        firestore.collection("notes")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("myNotes")
            .document(noteId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show()
                navigateBackToNotesActivity()
            }
            .addOnFailureListener { Toast.makeText(this, "Failed to Delete Note", Toast.LENGTH_SHORT).show() }
    }

    private fun navigateBackToNotesActivity() {
        val intent = Intent(this, notesactivity::class.java)
        startActivity(intent)
        finish()
    }
}