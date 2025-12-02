package com.example.ayos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class LguProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var assignedArea: TextView
    private lateinit var btnEditProfile: LinearLayout
    private lateinit var btnSettings: LinearLayout
    private lateinit var btnLogout: LinearLayout

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_lgu_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvName)
        tvRole = view.findViewById(R.id.tvRole)
        assignedArea = view.findViewById(R.id.assignedArea)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnSettings = view.findViewById(R.id.btnSettings)
        btnLogout = view.findViewById(R.id.btnLogout)

        db = FirebaseFirestore.getInstance()

        loadLguData()

        btnEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ProfEdit())
                .addToBackStack(null)
                .commit()
        }

        btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ProfSet())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("UserSession", AppCompatActivity.MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadLguData()
    }

    private fun loadLguData() {
        val prefs = requireActivity().getSharedPreferences("UserSession", AppCompatActivity.MODE_PRIVATE)
        val phone = prefs.getString("loggedInPhone", null)

        if (phone != null) {
            // Load from "Users" collection (assuming shared) or "lgus"
            db.collection("Users").document(phone).get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.getString("role") == "lgu") {
                        val name = document.getString("name") ?: "Unknown"
                        val role = "LGU"  // Hardcoded or from doc
                        val assignedAreaValue = document.getString("assignedArea") ?: "N/A"  // Renamed to avoid conflict

                        tvName.text = name
                        tvRole.text = role
                        assignedArea.text = "Assigned Area: $assignedAreaValue"  // Set on TextView
                    } else {
                        Toast.makeText(requireContext(), "LGU profile data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load LGU profile data.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No user logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}