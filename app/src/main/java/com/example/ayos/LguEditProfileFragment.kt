package com.example.ayos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LguEditProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLocation: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgu_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.etNameValue)
        etEmail = view.findViewById(R.id.etEmailValue)
        etPhone = view.findViewById(R.id.etPhoneValue)
        etLocation = view.findViewById(R.id.etLocationValue)

        val returnBtn = view.findViewById<ImageView>(R.id.returnBtn)
        val saveBtn = view.findViewById<Button>(R.id.saveBtn)

        setInitialUserData()

        saveBtn.setOnClickListener {
            saveUserData()
        }

        returnBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setInitialUserData() {
        // Load existing user data from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        etName.setText(sharedPreferences.getString("name", ""))
        etEmail.setText(sharedPreferences.getString("email", ""))
        etPhone.setText(sharedPreferences.getString("phone", ""))
        etLocation.setText(sharedPreferences.getString("location", ""))
    }

    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val location = etLocation.text.toString().trim()

        // Clear previous errors
        clearErrors()
        if (name.isEmpty()) {
            etName.error = "Name is required"
            return
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email is required"
            return
        } else if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            return
        } else if (location.isEmpty()) {
            etLocation.error = "Location is required"
            return
        }

        saveToSharedPreferences(name, email, phone, location)

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()

        // Navigate back
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun clearErrors() {
        etName.error = null
        etEmail.error = null
        etPhone.error = null
        etLocation.error = null
    }

    private fun saveToSharedPreferences(name: String, email: String, phone: String, location: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("name", name)
            putString("email", email)
            putString("phone", phone)
            putString("location", location)
            apply() // Don't forget to call apply() or commit()
        }
    }
}