package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class notesactivity : AppCompatActivity() {

    private lateinit var createNotesFab: FloatingActionButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesactivity)

        // Initialize Firebase Authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        // Set up the Toolbar as Action Bar
        val toolbar: Toolbar = findViewById(R.id.toolbar) // Ensure you have a Toolbar in your layout
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Notes"

        // Enable the home button in the toolbar (for hamburger icon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Query to fetch notes
        val query = firestore.collection("notes")
            .document(firebaseUser.uid)
            .collection("myNotes")
            .orderBy("title", Query.Direction.ASCENDING)

        // Set up FirestoreRecyclerOptions
        val options = FirestoreRecyclerOptions.Builder<firebaseModel>()
            .setQuery(query, firebaseModel::class.java)
            .build()

        // Initialize FirestoreRecyclerAdapter
        noteAdapter = object : FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_layout, parent, false)
                return NoteViewHolder(view)
            }

            override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: firebaseModel) {
                holder.bind(model)

                // Handle note click to open NoteDetailActivity
                holder.itemView.setOnClickListener {
                    val intent = Intent(this@notesactivity, NoteDetailActivity::class.java)
                    intent.putExtra("NOTE_ID", noteAdapter.snapshots.getSnapshot(position).id)
                    intent.putExtra("NOTE_TITLE", model.title)
                    intent.putExtra("NOTE_CONTENT", model.content)
                    startActivity(intent)
                }
            }
        }

        // Set adapter for RecyclerView
        recyclerView.adapter = noteAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Set up Floating Action Button for creating a new note
        createNotesFab = findViewById(R.id.createnotefab)
        createNotesFab.setOnClickListener {
            val intent = Intent(this@notesactivity, createnote::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu for the ActionBar
        menuInflater.inflate(R.menu.menu, menu) // Ensure your menu.xml contains the logout option
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle logout when home button (hamburger icon) is clicked
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to handle logout
    private fun performLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this@notesactivity, MainActivity::class.java))
        finish()
    }

    // ViewHolder class to display each note
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val contentTextView: TextView = itemView.findViewById(R.id.notecontent)

        fun bind(note: firebaseModel) {
            titleTextView.text = note.title
            contentTextView.text = note.content
        }
    }
}
