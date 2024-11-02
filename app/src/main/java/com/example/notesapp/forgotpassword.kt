package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notesapp.R.*
import com.google.firebase.auth.FirebaseAuth

class forgotpassword : AppCompatActivity() {
    private lateinit var mforgotpassword: EditText
    private lateinit var mpasswordrecoverbutton: Button
    private lateinit var mgobacktologin: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_forgotpassword)

        supportActionBar?.hide()
        firebaseAuth=FirebaseAuth.getInstance()

        mforgotpassword = findViewById(id.forgotpassword)
        mpasswordrecoverbutton = findViewById(id.passwordrecoverbutton)
        mgobacktologin = findViewById(id.gobacktologin)

        mgobacktologin.setOnClickListener {
            val intent = Intent(this@forgotpassword, MainActivity::class.java)
            startActivity(intent)
        }

        mpasswordrecoverbutton.setOnClickListener {
            val mail = mforgotpassword.text.toString().trim()
            if (mail.isEmpty()) {
                Toast.makeText(applicationContext, "Enter your mail first", Toast.LENGTH_SHORT).show()
            } else {
                // Code to send password recovery email
                firebaseAuth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "Password recovery email sent", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        else{
                            Toast.makeText(applicationContext, "Failed to send password recovery email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}