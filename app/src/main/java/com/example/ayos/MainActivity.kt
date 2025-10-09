package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val loggedInPhone = prefs.getString("loggedInPhone", null)
        val loggedInRole = prefs.getString("loggedInRole", null)

        if (loggedInPhone != null && loggedInRole != null) {
            // User already logged in → redirect to dashboard
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

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}