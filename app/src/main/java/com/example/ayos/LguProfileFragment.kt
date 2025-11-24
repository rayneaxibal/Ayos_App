package com.example.ayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LguProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgu_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can add click listeners for the menu items here
        val editProfileRow = view.findViewById<View>(R.id.editProfileRow)
        val settingsRow = view.findViewById<View>(R.id.settingsRow)
        val logoutRow = view.findViewById<View>(R.id.logoutRow)

        editProfileRow.setOnClickListener {
            // Handle edit profile click
            // Example: navigate to EditProfileFragment
            /*
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, EditProfileFragment())
                .addToBackStack(null)
                .commit()
            */
        }

        settingsRow.setOnClickListener {
            // Handle settings click
            // Example: navigate to SettingsFragment
            /*
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, SettingsFragment())
                .addToBackStack(null)
                .commit()
            */
        }

        logoutRow.setOnClickListener {
            // Handle logout click
            // Example: show logout confirmation dialog
            /*
            showLogoutConfirmationDialog()
            */
        }

        // You can also update the profile data dynamically here
        // updateProfileData()
    }

    /*
    private fun updateProfileData() {
        // Example: Update profile information from backend or shared preferences
        val tvName = view?.findViewById<TextView>(R.id.tvName5)
        val tvRole = view?.findViewById<TextView>(R.id.tvRole5)
        val tvTotalReports = view?.findViewById<TextView>(R.id.tvTotalReports)
        val tvDailyReport = view?.findViewById<TextView>(R.id.tvDailyReport)

        tvName?.text = "User Name"
        tvRole?.text = "LGU Member"
        tvTotalReports?.text = "5"
        tvDailyReport?.text = "2/5"
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, which ->
                // Perform logout logic
                // Example: clear session and navigate to login
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
    */
}