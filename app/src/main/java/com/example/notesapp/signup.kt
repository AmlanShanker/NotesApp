package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class signup : AppCompatActivity() {
    private lateinit var msignupemail: EditText
    private lateinit var msignuppassword: EditText
    private lateinit var msignupConfirmPassword: EditText
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

        msignupemail = findViewById(R.id.signupemail)
        msignuppassword = findViewById(R.id.signuppassword)
        msignupConfirmPassword = findViewById(R.id.signupConfirmPassword)
        msignup = findViewById(R.id.signup)
        mgotologin = findViewById(R.id.gotologin)
        firebaseAuth = FirebaseAuth.getInstance()

        mgotologin.setOnClickListener {
            val intent = Intent(this@signup, MainActivity::class.java)
            startActivity(intent)
        }

        msignup.setOnClickListener {
            val mail = msignupemail.text.toString().trim()
            val password = msignuppassword.text.toString().trim()
            val confirmPassword = msignupConfirmPassword.text.toString().trim()

            if (mail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(applicationContext, "All Fields are Required", Toast.LENGTH_SHORT).show()
            } else if (password.length < 7) {
                Toast.makeText(applicationContext, "Password Should Be Greater than 7 Characters", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(applicationContext, "Passwords Do Not Match", Toast.LENGTH_SHORT).show()
            } else {

                firebaseAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_SHORT).show()
                            sendEmailVerification()
                        } else {
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
                    Toast.makeText(applicationContext, "Verification Email Sent. Verify and Log In Again", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Failed to Send Verification Email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}