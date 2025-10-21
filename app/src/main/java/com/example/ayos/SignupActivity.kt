package com.example.ayos

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val inputName = findViewById<EditText>(R.id.inputName)
        val inputPhone = findViewById<EditText>(R.id.inputPhone)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val inputInviteCode = findViewById<EditText>(R.id.inputInviteCode)
        val btnSignup = findViewById<ImageButton>(R.id.btnSignup)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<User>>() {}.type

        btnSignup.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users: MutableList<User> =
                gson.fromJson(prefs.getString("users", "[]"), type) ?: mutableListOf()

            if (users.any { it.phone == phone }) {
                Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            users.add(User(name, phone, password, role))

            prefs.edit().apply {
                putString("users", gson.toJson(users))
                apply()
            }

            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}