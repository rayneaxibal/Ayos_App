package com.example.ayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfEdit : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var btnConfirm: Button
    private lateinit var btnBack: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prof_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        locationEditText = view.findViewById(R.id.locationEditText)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnBack = view.findViewById(R.id.btnBack)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    nameEditText.setText(snapshot.child("name").value?.toString() ?: "")
                    emailEditText.setText(snapshot.child("email").value?.toString() ?: "")
                    phoneEditText.setText(snapshot.child("phone").value?.toString() ?: "")
                    locationEditText.setText(snapshot.child("location").value?.toString() ?: "")
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show()
            }
        }

        btnConfirm.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (userId != null) {
                val updatedData = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "location" to location
                )

                database.child("Users").child(userId).updateChildren(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // go back to ProfileFragment
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}