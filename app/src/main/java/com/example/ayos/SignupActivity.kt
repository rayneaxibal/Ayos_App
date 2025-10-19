package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val inputName = findViewById<EditText>(R.id.inputName)
        val inputPhone = findViewById<EditText>(R.id.inputPhone)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val inputInviteCode = findViewById<EditText>(R.id.inputInviteCode)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val linkToLogin = findViewById<TextView>(R.id.linkToLogin)

        val roles = arrayOf("Resident", "LGU")
        val adapter = ArrayAdapter(this, R.layout.spinner_selected, roles)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        roleSpinner.adapter = adapter


        roleSpinner.adapter = adapter

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                inputInviteCode.visibility =
                    if (roles[position] == "LGU") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSignup.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (role == "LGU") {
                val invite = inputInviteCode.text.toString().trim()
                if (invite != "SECRET123") {
                    Toast.makeText(this, "Invalid LGU invitation code", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val gson = Gson()
            val type = object : TypeToken<MutableList<User>>() {}.type
            val existingUsers: MutableList<User> =
                gson.fromJson(prefs.getString("users", "[]"), type) ?: mutableListOf()

            if (existingUsers.any { it.phone == phone }) {
                Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(
                name = name,
                phone = phone,
                password = password,
                role = role,
                inviteCode = if (role == "LGU") inputInviteCode.text.toString().trim() else null
            )

            existingUsers.add(newUser)

            prefs.edit{ putString("users", gson.toJson(existingUsers)) }

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        linkToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}