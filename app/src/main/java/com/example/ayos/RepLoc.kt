package com.example.ayos // Make sure this matches your actual package name

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ayos.databinding.FragmentRepLocBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class RepLoc : AppCompatActivity(), OnMapReadyCallback {

    // --- Variables for Map and Location ---
    private var googleMap: GoogleMap? = null
    private lateinit var binding: FragmentRepLocBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isInitialCameraMoveDone = false

    // --- Variables for Saving Data and UI State ---
    private lateinit var sharedPreferences: SharedPreferences
    private val keySavedLat = "saved_location_latitude"
    private val keySavedLng = "saved_location_longitude"
    private var temporaryMarker: Marker? = null

    // [PERMISSION & GPS CHECK] Handles the result of the permission dialog.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission denied. Showing default location.", Toast.LENGTH_LONG).show()
                setupMapForDefaultLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRepLocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("LocationPrefs", MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // [SAVE & SHOW FRAGMENT] Set up the click listener for the Submit Button.
        binding.btnSubmitLocation.setOnClickListener {
            temporaryMarker?.let { marker ->
                // ✅ FIX: Permission check added here to solve the compiler error when calling the function below.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    saveLocationAndShowFragment(marker.position)
                } else {
                    Toast.makeText(this, "Permission is required to submit a location.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // [LIVE LOCATION] Define what happens when a new live location is received.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)

                // NEW: Smoothly animate the camera to the current location only once initially
                if (!isInitialCameraMoveDone) {
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    isInitialCameraMoveDone = true
                }
            }
        }

        // Load the map fragment.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Toast.makeText(this, "Tap on the map to choose a location.", Toast.LENGTH_LONG).show()

        // [TAP-TO-CHANGE] Set listener for single taps.
        googleMap?.setOnMapClickListener { latLng ->
            temporaryMarker?.remove() // Remove old pin
            temporaryMarker = googleMap?.addMarker( // Add new pin
                MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
            )
            binding.btnSubmitLocation.visibility = View.VISIBLE // Show button
        }

        // Load a previously saved location if it exists, otherwise get current location.
        if (!loadSavedLocation()) {
            checkLocationPermission()
        }
    }

    private fun saveLocationAndShowFragment(latLng: LatLng) {
        // 1. Save the location data
        sharedPreferences.edit().apply {
            putString(keySavedLat, latLng.latitude.toString())
            putString(keySavedLng, latLng.longitude.toString())
            apply()
        }
        Toast.makeText(this, "Location Submitted!", Toast.LENGTH_SHORT).show()

        // 2. Hide the UI and lock the map
        binding.btnSubmitLocation.visibility = View.GONE
        googleMap?.setOnMapClickListener(null) // Disable further taps

        // This permission check is required by the compiler to disable the blue dot
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap?.isMyLocationEnabled = false // Disable blue dot
            } catch (e: SecurityException) {
                e.printStackTrace() // Log error for safety
            }
        }

        // --- ✅ THE FIX: REDIRECT TO THE DASHBOARD ---
        // 3. Create an Intent to launch ResDashboardActivity
        val intent = Intent(this, ResDashboardActivity::class.java)

        // 4. Start the new activity
        startActivity(intent)

        // 5. IMPORTANT: Finish the current activity so the user cannot press "Back" to return to it
        finish()
    }


    private fun loadSavedLocation(): Boolean {
        val latString = sharedPreferences.getString(keySavedLat, null)
        val lngString = sharedPreferences.getString(keySavedLng, null)

        if (latString != null && lngString != null) {
            val savedLatLng = LatLng(latString.toDouble(), lngString.toDouble())
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, 15f))
            temporaryMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(savedLatLng)
                    .title("Previously Saved Location")
            )
            binding.btnSubmitLocation.visibility = View.VISIBLE
            // Also show the user's current location with the blue dot.
            checkLocationPermission()
            return true
        }
        return false
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startLocationUpdates() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Please turn on your device's location service.", Toast.LENGTH_LONG).show()
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        // ✅ FIX: Check for BOTH permissions explicitly (as per lint's "or" requirement)
        val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {  // Proceed if at least one is granted
            try {
                // NEW: Immediately get the last known location and move the camera there (shows blue dot and centers map quickly)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        isInitialCameraMoveDone = true  // Prevent further automatic moves
                    } ?: run {
                        // If no last location, fall back to default
                        setupMapForDefaultLocation()
                    }
                }

                // This enables the blue dot and the built-in "My Location" button (compass icon in top-right)
                googleMap?.isMyLocationEnabled = true

                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Toast.makeText(this, "Location permission required for live updates.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Location permissions are required.", Toast.LENGTH_SHORT).show()
            setupMapForDefaultLocation()
        }
    }

    private fun setupMapForDefaultLocation() {
        val baguioCity = LatLng(16.4023, 120.5960)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(baguioCity, 12f))
        googleMap?.addMarker(MarkerOptions().position(baguioCity).title("Default Location: Baguio City"))
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        // Only resume location updates if a location has not been finally submitted yet.
        // We check this by seeing if the click listener is still active.
        if (googleMap?.isMyLocationEnabled == true) {
            checkLocationPermission()
        }
    }
}

