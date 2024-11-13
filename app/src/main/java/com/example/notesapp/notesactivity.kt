package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.View
import android.widget.EditText
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
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesactivity)


        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Notes"


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        searchBar = findViewById(R.id.search_bar)


        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchNotes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        fetchNotes()


        createNotesFab = findViewById(R.id.createnotefab)
        createNotesFab.setOnClickListener {
            val intent = Intent(this@notesactivity, createnote::class.java)
            startActivity(intent)
        }
    }

    private fun fetchNotes(queryText: String = "") {

        val query = if (queryText.isEmpty()) {
            firestore.collection("notes")
                .document(firebaseUser.uid)
                .collection("myNotes")
                .orderBy("title", Query.Direction.ASCENDING)
        } else {
            firestore.collection("notes")
                .document(firebaseUser.uid)
                .collection("myNotes")
                .orderBy("title")
                .startAt(queryText)
                .endAt(queryText + "\uf8ff")
        }


        val options = FirestoreRecyclerOptions.Builder<firebaseModel>()
            .setQuery(query, firebaseModel::class.java)
            .build()


        noteAdapter = object : FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_layout, parent, false)
                return NoteViewHolder(view)
            }

            override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: firebaseModel) {
                holder.bind(model)


                holder.itemView.setOnClickListener {
                    val intent = Intent(this@notesactivity, NoteDetailActivity::class.java)
                    intent.putExtra("NOTE_ID", noteAdapter.snapshots.getSnapshot(position).id)
                    intent.putExtra("NOTE_TITLE", model.title)
                    intent.putExtra("NOTE_CONTENT", model.content)
                    startActivity(intent)
                }
            }
        }


        recyclerView.adapter = noteAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun searchNotes(queryText: String) {

        noteAdapter.stopListening()
        fetchNotes(queryText)
        noteAdapter.startListening()
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

        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                performLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun performLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this@notesactivity, MainActivity::class.java))
        finish()
    }


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val contentTextView: TextView = itemView.findViewById(R.id.notecontent)

        fun bind(note: firebaseModel) {
            titleTextView.text = note.title
            contentTextView.text = note.content
        }
    }
}
