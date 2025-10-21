package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val loggedInPhone = prefs.getString("loggedInPhone", null)
        val loggedInRole = prefs.getString("loggedInRole", null)

        // ✅ Check if user is already logged in
        if (loggedInPhone != null && loggedInRole != null) {
            // Redirect user to the correct dashboard based on role
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

        // ✅ Default: show the login/signup screen (activity_main)
        setContentView(R.layout.activity_main)

        // Load ProfileFragment only the first time (optional)
        if (savedInstanceState == null) {
            loadFragment(ProfileFragment.newInstance("param1", "param2"))
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // ✅ Function to load a fragment into the activity
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Make sure activity_main has this container
            .addToBackStack(null)
            .commit()
    }
}