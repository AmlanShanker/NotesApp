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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class createnote : AppCompatActivity() {
    private lateinit var mcreateTitleOfNote: EditText
    private lateinit var mcreateContentOfNote: EditText
    private lateinit var mSaveNote: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var noteId: String? = null

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


        noteId = intent.getStringExtra("NOTE_ID")

        if (!noteId.isNullOrEmpty()) {
            loadNoteDetails(noteId!!)
        }

        mSaveNote.setOnClickListener {
            val title = mcreateTitleOfNote.text.toString()
            val content = mcreateContentOfNote.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(applicationContext, "Both fields are required", Toast.LENGTH_SHORT).show()
            } else {
                if (noteId != null) {
                    updateNote(noteId!!, title, content)
                } else {
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
            .document()

        val currentDateTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        val note = hashMapOf(
            "title" to title,
            "content" to content,
            "timestamp" to currentDateTime
        )

        documentReference.set(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note Created Successfully on $currentDateTime", Toast.LENGTH_SHORT).show()
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
                val currentDateTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                Toast.makeText(this, "Note Updated Successfully on $currentDateTime", Toast.LENGTH_SHORT).show()
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