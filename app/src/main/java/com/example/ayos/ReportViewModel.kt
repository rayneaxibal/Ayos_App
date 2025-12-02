package com.example.ayos

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class ReportViewModel : ViewModel() {
    var category: String? = null
    var description: String? = null
    var imageUri: Uri? = null
    var location: GeoPoint? = null
}