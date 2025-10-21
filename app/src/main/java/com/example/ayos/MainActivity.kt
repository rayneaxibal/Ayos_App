package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val loggedInPhone = prefs.getString("loggedInPhone", null)
        val loggedInRole = prefs.getString("loggedInRole", null)

        // Auto-login if user info exists
        if (loggedInPhone != null && loggedInRole != null) {
            val intent = when (loggedInRole) {
                "Resident" -> Intent(this, ResDashboardActivity::class.java)
                "LGU" -> Intent(this, LguDashboardActivity::class.java)
                else -> null
            }
            intent?.let {
                startActivity(it)
                finish()
                return
            }
        }

        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<ImageButton>(R.id.btnLogin)
        val btnSignup = findViewById<ImageButton>(R.id.btnSignup)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}