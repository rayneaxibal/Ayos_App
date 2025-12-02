package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ayos.models.User
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("UserSession", MODE_PRIVATE)
        val savedPhone = prefs.getString("loggedInPhone", null)
        val savedRole = prefs.getString("loggedInRole", null)

        if (!savedPhone.isNullOrEmpty() && !savedRole.isNullOrEmpty()) {
            val intent = when (savedRole) {
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

        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        val loginPhone = findViewById<EditText>(R.id.loginPhone)
        val loginPassword = findViewById<EditText>(R.id.loginPassword)
        val btnLogin = findViewById<ImageButton>(R.id.btnLogin)
        val linkToSignup = findViewById<TextView>(R.id.linkToSignup)

        loginPhone.setText("+63")
        loginPhone.setSelection(loginPhone.text.length)

        loginPhone.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                if (!s.toString().startsWith("+63")) {
                    isUpdating = true
                    loginPhone.setText("+63")
                    loginPhone.setSelection(loginPhone.text.length)
                    isUpdating = false
                }
            }
        })

        btnLogin.setOnClickListener {
            val phone = loginPhone.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phone.matches(Regex("^\\+63\\d{10}$"))) {
                Toast.makeText(this, "Invalid phone number. Format: +639XXXXXXXXX", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("Users").document(phone).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        Toast.makeText(this, "Account not found. Please sign up first.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val user = document.toObject(User::class.java)
                    if (user == null) {
                        Toast.makeText(this, "Error loading user data.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    if (PasswordUtils.verify(password, user.password)) {
                        Toast.makeText(this, "Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()

                        val prefsEditor = getSharedPreferences("UserSession", MODE_PRIVATE).edit()
                        prefsEditor.putString("loggedInPhone", user.phone)
                        prefsEditor.putString("loggedInRole", user.role)
                        prefsEditor.putString("loggedInName", user.name)
                        prefsEditor.apply()

                        val intent = when (user.role) {
                            "Resident" -> Intent(this, ResDashboardActivity::class.java)
                            "LGU" -> Intent(this, LguDashboardActivity::class.java)
                            else -> {
                                Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                        }

                        intent?.let {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        linkToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}