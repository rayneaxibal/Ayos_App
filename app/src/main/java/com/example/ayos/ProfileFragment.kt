package com.example.ayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvRole = view.findViewById<TextView>(R.id.tvRole)
        val tvTotalReports = view.findViewById<TextView>(R.id.tvTotalReports)
        val tvDailyReport = view.findViewById<TextView>(R.id.tvDailyReport)

        val btnEditProfile = view.findViewById<LinearLayout>(R.id.btnEditProfile)
        val btnSettings = view.findViewById<LinearLayout>(R.id.btnSettings)
        val btnLogout = view.findViewById<LinearLayout>(R.id.btnLogout)

        // Dummy data or get from user session
        tvName.text = "Laira Mae Lucop"
        tvRole.text = "Resident"
        tvTotalReports.text = "2"
        tvDailyReport.text = "1/3"

        btnEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        }
    }
}