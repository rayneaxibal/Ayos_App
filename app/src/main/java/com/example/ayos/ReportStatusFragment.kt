package com.example.ayos

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt
import com.example.ayos.models.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class ReportStatusFragment : Fragment(R.layout.fragment_report_status) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private var reports = mutableListOf<Report>()

    private lateinit var btnSubmitted: TextView
    private lateinit var btnUnderReview: TextView
    private lateinit var btnInProgress: TextView
    private lateinit var btnCompleted: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSubmitted = view.findViewById(R.id.btn_submitted)
        btnUnderReview = view.findViewById(R.id.btn_under_review)
        btnInProgress = view.findViewById(R.id.btn_in_progress)
        btnCompleted = view.findViewById(R.id.btn_completed)

        recyclerView = view.findViewById(R.id.recyclerViewReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReportAdapter(
            allReports = reports,
            onViewClick = { report -> showReportDetails(report) },
            onEditClick = { report -> editReport(report) },
            onDeleteClick = { report -> deleteReport(report) }
        )
        recyclerView.adapter = adapter

        btnSubmitted.setOnClickListener { filterReports("Submitted") }
        btnUnderReview.setOnClickListener { filterReports("Under Review") }
        btnInProgress.setOnClickListener { filterReports("In Progress") }
        btnCompleted.setOnClickListener { filterReports("Completed") }

        loadUserReports()
    }

    private fun loadUserReports() {
        val prefs = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val currentUserId = prefs.getString("loggedInPhone", null)
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("reports")
            .whereEqualTo("userId", currentUserId)  // Filter by phone (userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                reports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(Report::class.java)
                    report?.let { reports.add(it) }
                }
                adapter.updateReports(reports)
                filterReports("All")  // Default to show all
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load reports: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterReports(status: String) {
        adapter.filterByStatus(status)
        updateButtonUI(status)
    }

    private fun updateButtonUI(activeFilter: String) {
        val activeBg = R.drawable.active
        val inactiveBg = R.drawable.inactive
        val activeText = "#FFFFFF"
        val inactiveText = "#FFFFFF"

        listOf(btnSubmitted, btnUnderReview, btnInProgress, btnCompleted).forEach { btn ->
            if (btn.text.toString() == activeFilter) {
                btn.setBackgroundResource(activeBg)
                btn.setTextColor(activeText.toColorInt())
            } else {
                btn.setBackgroundResource(inactiveBg)
                btn.setTextColor(inactiveText.toColorInt())
            }
        }
    }

    private fun showReportDetails(report: Report) {
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(report.timestamp))
        AlertDialog.Builder(requireContext())
            .setTitle("Report Details")
            .setMessage(
                """
                Category: ${report.category}
                Date: $dateStr
                Status: ${report.status}
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun editReport(report: Report) {  // For Update
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_report, null)
        val categoryEdit = dialogView.findViewById<EditText>(R.id.editCategory)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editDescription)
        val btnCont = dialogView.findViewById<ImageButton>(R.id.btnCont)

        categoryEdit.setText(report.category)
        descriptionEdit.setText(report.description)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCont.setOnClickListener {
            val newCategory = categoryEdit.text.toString().trim()
            val newDescription = descriptionEdit.text.toString().trim()
            if (newCategory.isNotEmpty() && newDescription.isNotEmpty()) {
                val updates = mapOf("category" to newCategory, "description" to newDescription)
                db.collection("reports").document(report.reportId).update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Report updated", Toast.LENGTH_SHORT).show()
                        loadUserReports()  // Refresh list
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun deleteReport(report: Report) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Report")
            .setMessage("Are you sure you want to delete this report?")
            .setPositiveButton("Yes") { _, _ ->
                db.collection("reports").document(report.reportId)
                    .delete()
                    .addOnSuccessListener {
                        reports.remove(report)
                        adapter.updateReports(reports)
                        Toast.makeText(requireContext(), "Report has been deleted.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to delete report: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }
}