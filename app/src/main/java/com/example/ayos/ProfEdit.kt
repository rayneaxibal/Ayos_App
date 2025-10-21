package com.example.ayos

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.ayos.databinding.FragmentProfEditBinding

class ProfEdit : AppCompatActivity() {

    private lateinit var binding: FragmentProfEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve and pre-fill data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        binding.nameEditText.setText(sharedPreferences.getString("name", "Default Name"))
        binding.emailEditText.setText(sharedPreferences.getString("email", "default@example.com"))
        binding.phoneEditText.setText(sharedPreferences.getString("phone", "000-000-0000"))
        binding.locationEditText.setText(sharedPreferences.getString("location", "Default Location"))

        // 🡨 Back Arrow → simply close this activity to return to ProfileFragment
        binding.btnBack.setOnClickListener {
            finish() // ✅ Returns to ProfileFragment (inside parent activity)
        }

        // ✅ Confirm Button → Save changes and go back to ProfileFragment
        binding.btnConfirm.setOnClickListener {
            sharedPreferences.edit {
                putString("name", binding.nameEditText.text.toString())
                putString("email", binding.emailEditText.text.toString())
                putString("phone", binding.phoneEditText.text.toString())
                putString("location", binding.locationEditText.text.toString())
            }
            finish() // ✅ Closes this screen and shows ProfileFragment again
        }

        // ✅ Handle physical back press properly
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // ✅ Same behavior — go back to ProfileFragment
            }
        })
    }
}