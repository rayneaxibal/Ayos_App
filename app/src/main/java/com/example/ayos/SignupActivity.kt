package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ayos.models.User
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = FirebaseFirestore.getInstance()

        val inputName = findViewById<EditText>(R.id.inputName)
        val inputPhone = findViewById<EditText>(R.id.inputPhone)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val inputInviteCode = findViewById<EditText>(R.id.inputInviteCode)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val btnSignup = findViewById<ImageButton>(R.id.btnSignup)
        val linkToLogin = findViewById<TextView>(R.id.linkToLogin)

        val roles = arrayOf("Resident", "LGU")
        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = roles[position]
                inputInviteCode.visibility = if (selectedRole == "LGU") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        linkToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSignup.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()
            val inviteCode = inputInviteCode.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!phone.matches(Regex("^\\+63\\d{10}$"))) {
                Toast.makeText(this, "Invalid phone format. Use +639XXXXXXXXX", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (role == "LGU") {
                if (inviteCode.isEmpty() || inviteCode != "APPROVED_LGU_CODE") {
                    Toast.makeText(this, "Invalid or missing invitation code for LGU", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            db.collection("Users").document(phone).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Toast.makeText(this, "Phone already registered", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val hashedPassword = PasswordUtils.hash(password)
                    val user = User(name = name, phone = phone, password = hashedPassword, role = role)

                    db.collection("Users").document(phone).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}