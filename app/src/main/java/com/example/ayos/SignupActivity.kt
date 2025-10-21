package com.example.ayos

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter

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

        val roles = listOf("Resident", "LGU")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRole = roles[position]
                if (selectedRole == "LGU") {
                    if (inputInviteCode.visibility != View.VISIBLE) {
                        inputInviteCode.visibility = View.VISIBLE
                        val slideIn = TranslateAnimation(0f, 0f, -50f, 0f)
                        slideIn.duration = 300
                        val fadeIn = AlphaAnimation(0f, 1f)
                        fadeIn.duration = 300
                        inputInviteCode.startAnimation(slideIn)
                        inputInviteCode.startAnimation(fadeIn)
                    }
                } else {
                    if (inputInviteCode.visibility == View.VISIBLE) {
                        val slideOut = TranslateAnimation(0f, 0f, 0f, -50f)
                        slideOut.duration = 250
                        val fadeOut = AlphaAnimation(1f, 0f)
                        fadeOut.duration = 250
                        inputInviteCode.startAnimation(slideOut)
                        inputInviteCode.startAnimation(fadeOut)
                        inputInviteCode.postDelayed({
                            inputInviteCode.visibility = View.GONE
                        }, 250)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSignup.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()
            val inviteCode = inputInviteCode.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (role == "LGU" && inviteCode != "LGU123") {
                Toast.makeText(this, "Invalid invitation code", Toast.LENGTH_SHORT).show()
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