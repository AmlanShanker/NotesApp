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

class MainActivity : AppCompatActivity() {
    private lateinit var mloginemail: EditText
    private lateinit var mloginpassword: EditText
    private lateinit var mlogin: RelativeLayout
    private lateinit var mgotosignup: RelativeLayout
    private lateinit var mgotoforgotpassword: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mloginemail= findViewById(R.id.loginemail)
        mloginpassword= findViewById(R.id.loginpassword)
        mlogin = findViewById(R.id.login)
        mgotosignup = findViewById(R.id.gotosignup)
        mgotoforgotpassword = findViewById(R.id.gotoforgotpassword)
        firebaseAuth=FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            finish()
            startActivity(Intent(this, notesactivity::class.java))
        }


        mgotosignup.setOnClickListener {
            val intent = Intent(this@MainActivity, signup::class.java)
            startActivity(intent)
        }

        mgotoforgotpassword.setOnClickListener {
            val intent = Intent(this@MainActivity, forgotpassword::class.java)
            startActivity(intent)
        }
        mlogin.setOnClickListener {
            val mail = mloginemail.text.toString().trim()
            val password = mloginpassword.text.toString().trim()

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "All Fields Are Required", Toast.LENGTH_SHORT).show()
            } else {
                // Login the user
                firebaseAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            checkMailVerification()
                        } else {
                            Toast.makeText(applicationContext, "Account Doesn't Exist", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

    }
    private fun checkMailVerification() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser?.isEmailVerified == true) {
            Toast.makeText(applicationContext, "Logged In", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, notesactivity::class.java))
        } else {
            Toast.makeText(applicationContext, "Verify your mail first", Toast.LENGTH_SHORT).show()
            firebaseAuth.signOut()
        }
    }

}