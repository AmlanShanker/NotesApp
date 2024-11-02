package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class signup : AppCompatActivity() {
    private lateinit var msignupemail: EditText
    private lateinit var msignuppassword: EditText
    private lateinit var msignup: RelativeLayout
    private lateinit var mgotologin: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        msignupemail= findViewById(R.id.signupemail)
        msignup = findViewById(R.id.signup)
        msignuppassword = findViewById(R.id.signuppassword)
        mgotologin = findViewById(R.id.gotologin)

        firebaseAuth=FirebaseAuth.getInstance()

        mgotologin.setOnClickListener {
            val intent = Intent(this@signup, MainActivity::class.java)
            startActivity(intent)
        }
        msignup.setOnClickListener {
            val mail = msignupemail.text.toString().trim()
            val password = msignuppassword.text.toString().trim()

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "All Fields are Required", Toast.LENGTH_SHORT).show()
            } else if (password.length < 7) {
                Toast.makeText(applicationContext, "Password Should Be Greater than 7 Digits", Toast.LENGTH_SHORT).show()
            } else {
                // Register the user to Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_SHORT).show()
                            sendEmailVerification()
                        }
                        else{
                            Toast.makeText(applicationContext, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

    }
    private fun sendEmailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Verification Email is Sent. Verify and Log In Again", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Failed To Send Verification Email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}