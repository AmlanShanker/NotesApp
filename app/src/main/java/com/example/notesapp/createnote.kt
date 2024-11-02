package com.example.notesapp

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class createnote : AppCompatActivity() {
    private lateinit var mcreateTitleOfNote: EditText
    private lateinit var mcreateContentOfNote: EditText
    private lateinit var mSaveNote: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseFirestore: FirebaseFirestore

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
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        mSaveNote.setOnClickListener {
            val title = mcreateTitleOfNote.text.toString()
            val content = mcreateContentOfNote.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(applicationContext, "Both fields are required", Toast.LENGTH_SHORT).show()
            } else {
                // Get a reference to a new document within "myNotes"
                val documentReference = firebaseFirestore.collection("notes")
                    .document(firebaseUser.uid)
                    .collection("myNotes")
                    .document() // This generates a new document ID automatically

                val note = hashMapOf(
                    "title" to title,
                    "content" to content
                )

                documentReference.set(note)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Note Created Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@createnote, notesactivity::class.java))
                    }
                    .addOnFailureListener { e: Exception ->
                        Toast.makeText(this, "Failed to Create Note", Toast.LENGTH_SHORT).show()

                    }
            }
        }



    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}