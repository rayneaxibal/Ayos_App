package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginPhone = findViewById<EditText>(R.id.loginPhone)
        val loginPassword = findViewById<EditText>(R.id.loginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val linkToSignup = findViewById<TextView>(R.id.linkToSignup)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<User>>() {}.type

        btnLogin.setOnClickListener {
            val phone = loginPhone.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            // checks if fields are empty
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users: MutableList<User> = gson.fromJson(prefs.getString("users", "[]"), type) ?: mutableListOf()
            val user = users.find { it.phone == phone && it.password == password }

            if (user != null) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                // Save logged-in info
                prefs.edit().apply {
                    putString("loggedInPhone", user.phone)
                    putString("loggedInRole", user.role)
                    apply()
                }

                // Role-based redirection
                val intent = when (user.role) {
                    "Resident" -> Intent(this, ResDashboardActivity::class.java)
                    "LGU" -> Intent(this, LguDashboardActivity::class.java)
                    else -> null
                }

                intent?.let { startActivity(it) }
                finish()
            } else {
                Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show()
            }
        }

        linkToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}