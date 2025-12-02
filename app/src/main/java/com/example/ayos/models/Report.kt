package com.example.ayos.models

data class Report(
    var reportId: String = "",
    var userId: String = "",
    var category: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var status: String = "Submitted",
    var timestamp: Long = System.currentTimeMillis()
)