package com.example.ayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfSet : Fragment() {

    private lateinit var themeValue: TextView
    private lateinit var languageValue: TextView
    private lateinit var btnBack: ImageView

    private lateinit var database: DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prof_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeValue = view.findViewById(R.id.themeValue)
        languageValue = view.findViewById(R.id.languageValue)
        btnBack = view.findViewById(R.id.btnBack)

        database = FirebaseDatabase.getInstance().getReference("Users")

        loadSettings()

        // Change theme when clicked
        themeValue.setOnClickListener {
            val newTheme = if (themeValue.text == "Light") "Dark" else "Light"
            themeValue.text = newTheme
            saveSetting("theme", newTheme)
        }

        // Change language when clicked
        languageValue.setOnClickListener {
            val newLanguage = if (languageValue.text == "English") "Filipino" else "English"
            languageValue.text = newLanguage
            saveSetting("language", newLanguage)
        }

        // Navigate back
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSettings() {
        val userId = auth.currentUser?.uid ?: return

        database.child(userId).child("settings").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val theme = snapshot.child("theme").getValue(String::class.java) ?: "Light"
                    val language = snapshot.child("language").getValue(String::class.java) ?: "English"
                    themeValue.text = theme
                    languageValue.text = language
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load settings", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveSetting(key: String, value: String) {
        val userId = auth.currentUser?.uid ?: return

        database.child(userId).child("settings").child(key).setValue(value)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$key updated to $value", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update $key", Toast.LENGTH_SHORT).show()
            }
    }
}