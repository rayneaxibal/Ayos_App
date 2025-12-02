package com.example.ayos

import com.example.ayos.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class ReportHelper {

    private val db = FirebaseFirestore.getInstance()
    private val reportsRef = db.collection("reports")  // Changed to lowercase "reports" for consistency

    fun createReport(report: Report, onComplete: (Boolean) -> Unit) {
        val id = reportsRef.document().id
        report.reportId = id
        reportsRef.document(id)
            .set(report)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getAllReports(onResult: (List<Report>) -> Unit) {
        reportsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(Report::class.java)
                onResult(list)
            }
        }
    }

    fun updateReport(reportId: String, data: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        reportsRef.document(reportId)
            .update(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteReport(reportId: String, onComplete: (Boolean) -> Unit) {
        reportsRef.document(reportId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}