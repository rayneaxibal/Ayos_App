package com.example.ayos

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class ProfSet : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_prof_set) // Make sure this is the correct layout file

        // 🔙 Back Arrow → return to ProfileFragment (via parent Activity)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // ✅ Just close this Activity and go back to ProfileFragment
        }

        // ✅ Handle system back press (gesture or button)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // ✅ Same behavior as the back button — go back to ProfileFragment
            }
        })
    }
}