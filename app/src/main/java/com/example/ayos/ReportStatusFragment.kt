package com.example.ayos

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class ReportStatusFragment : Fragment(R.layout.fragment_report_status) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var reports: List<Report>

    private lateinit var btnSubmitted: TextView
    private lateinit var btnUnderReview: TextView
    private lateinit var btnInProgress: TextView
    private lateinit var btnCompleted: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSubmitted = view.findViewById(R.id.btn_submitted)
        btnUnderReview = view.findViewById(R.id.btn_under_review)
        btnInProgress = view.findViewById(R.id.btn_in_progress)
        btnCompleted = view.findViewById(R.id.btn_completed)

        // Sample reports
        reports = listOf(
            Report("Damaged Road", "Sep 18, 2025", "Submitted"),
            Report("Streetlight Issue", "Sep 19, 2025", "In Progress"),
            Report("Illegal Dumping", "Sep 20, 2025", "Under Review"),
            Report("Cleaned Canal", "Sep 21, 2025", "Completed")
        )

        recyclerView = view.findViewById(R.id.recyclerViewReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReportAdapter(reports) { report ->
        }
        recyclerView.adapter = adapter

        btnSubmitted.setOnClickListener { filterReports("Submitted") }
        btnUnderReview.setOnClickListener { filterReports("Under Review") }
        btnInProgress.setOnClickListener { filterReports("In Progress") }
        btnCompleted.setOnClickListener { filterReports("Completed") }

        filterReports("All")
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
}