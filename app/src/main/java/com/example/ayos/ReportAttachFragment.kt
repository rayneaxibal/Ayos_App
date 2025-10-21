package com.example.ayos

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ReportAttachFragment : Fragment(R.layout.fragment_report_attach) {

    private lateinit var uploadButton: ImageButton
    private lateinit var locationPin: ImageButton
    private lateinit var submitButton: ImageButton
    private lateinit var backButton: ImageButton

    // --- Media pickers ---
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Toast.makeText(requireContext(), "File selected!", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                Toast.makeText(requireContext(), "Photo captured!", Toast.LENGTH_SHORT).show()
            }
        }

    // --- Permission launchers (modern way) ---
    private val requestGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) pickImageLauncher.launch("image/*")
            else Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT).show()
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) takePhotoLauncher.launch(null)
            else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadButton = view.findViewById(R.id.uploadButton)
        locationPin = view.findViewById(R.id.locationPin)
        submitButton = view.findViewById(R.id.submitButton)
        backButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        uploadButton.setOnClickListener { showAttachOptions() }

        locationPin.setOnClickListener {
            Toast.makeText(requireContext(), "Location pin clicked!", Toast.LENGTH_SHORT).show()
        }

        submitButton.setOnClickListener {
            Toast.makeText(requireContext(), "Report has been submitted", Toast.LENGTH_SHORT).show()
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, homeFragment)
                .commit()
        }
    }

    // --- Helper methods ---
    private fun showAttachOptions() {
        val options = arrayOf("Gallery", "Camera")
        AlertDialog.Builder(requireContext())
            .setTitle("Attach Media")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkGalleryPermissionAndPick()
                    1 -> checkCameraPermissionAndCapture()
                }
            }
            .show()
    }

    private fun checkGalleryPermissionAndPick() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageLauncher.launch("image/*")
        } else {
            requestGalleryPermission.launch(permission)
        }
    }

    private fun checkCameraPermissionAndCapture() {
        val permission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            takePhotoLauncher.launch(null)
        } else {
            requestCameraPermission.launch(permission)
        }
    }
}