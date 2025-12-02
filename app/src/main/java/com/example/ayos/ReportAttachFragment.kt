package com.example.ayos

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ayos.models.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ReportAttachFragment : Fragment(R.layout.fragment_report_attach) {

    private lateinit var uploadButton: ImageButton
    private lateinit var submitButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var mapView: MapView
    private var selectedImageUri: Uri? = null
    private var selectedLocation: GeoPoint? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var viewModel: ReportViewModel

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                viewModel.imageUri = it
                Toast.makeText(requireContext(), "File selected!", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            bitmap?.let {
                selectedImageUri = null
                viewModel.imageUri = null
                Toast.makeText(requireContext(), "Photo captured!", Toast.LENGTH_SHORT).show()
            }
        }

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

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ReportViewModel::class.java)

        uploadButton = view.findViewById(R.id.uploadButton)
        submitButton = view.findViewById(R.id.submitButton)
        backButton = view.findViewById(R.id.backButton)
        mapView = view.findViewById(R.id.mapView)

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(14.0)

        val defaultLocation = viewModel.location ?: GeoPoint(16.4023, 120.5960)
        mapView.controller.setCenter(defaultLocation)

        var currentMarker: Marker? = null

        mapView.overlays.add(object : org.osmdroid.views.overlay.Overlay() {
            override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                e?.let {
                    val projection = mapView?.projection
                    val geoPoint = projection?.fromPixels(e.x.toInt(), e.y.toInt()) as? GeoPoint
                    geoPoint?.let { point ->

                        currentMarker?.let { oldMarker -> mapView.overlays.remove(oldMarker) }

                        val newMarker = Marker(mapView)
                        newMarker.position = point
                        newMarker.title = "Selected Location"
                        mapView.overlays.add(newMarker)
                        mapView.invalidate()

                        currentMarker = newMarker
                        selectedLocation = point
                        viewModel.location = point

                        Toast.makeText(
                            requireContext(),
                            "Selected location: ${point.latitude}, ${point.longitude}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return true
            }
        })

        backButton.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        uploadButton.setOnClickListener { showAttachOptions() }
        submitButton.setOnClickListener { submitReport() }
    }

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

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*")
        } else {
            requestGalleryPermission.launch(permission)
        }
    }

    private fun checkCameraPermissionAndCapture() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            takePhotoLauncher.launch(null)
        } else {
            requestCameraPermission.launch(permission)
        }
    }

    private fun submitReport() {
        val location = viewModel.location ?: selectedLocation
        if (location == null) {
            Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("loggedInPhone", null)
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val reportId = db.collection("reports").document().id

        val report = Report(
            reportId = reportId,
            userId = userId,
            category = viewModel.category ?: "",
            description = viewModel.description ?: "",
            imageUrl = "",
            latitude = location.latitude,
            longitude = location.longitude,
            status = "Submitted",
            timestamp = System.currentTimeMillis()
        )

        if (selectedImageUri != null) {
            val imageRef = storage.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        report.imageUrl = uri.toString()
                        saveReportToFirestore(report)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            saveReportToFirestore(report)
        }
    }

    private fun saveReportToFirestore(report: Report) {
        db.collection("reports")
            .document(report.reportId)
            .set(report)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Report submitted!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, HomeFragment())
                    .commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to submit report: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}