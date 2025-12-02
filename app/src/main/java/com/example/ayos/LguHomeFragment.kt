package com.example.ayos

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ayos.models.Report
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.core.graphics.toColorInt

class LguHomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var recentAdapter: RecentReportAdapter
    private var recentReports = mutableListOf<Report>()
    private var assignedBarangay: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgu_home, container, false)  // Use your provided XML
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        recentRecyclerView = view.findViewById(R.id.recyclerViewRecentReports)

        recentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recentAdapter = RecentReportAdapter(recentReports, ::onViewReport)
        recentRecyclerView.adapter = recentAdapter

        loadAssignedBarangay {
            loadPieChartData(pieChart)
            loadRecentReports()
        }
    }

    private fun loadAssignedBarangay(onComplete: () -> Unit) {
        val prefs = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userPhone = prefs.getString("loggedInPhone", null) ?: return

        db.collection("Users").document(userPhone).get()
            .addOnSuccessListener { doc ->
                assignedBarangay = doc.getString("assignedArea") ?: "Unknown"
                onComplete()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load assigned barangay", Toast.LENGTH_SHORT).show()
                onComplete()
            }
    }

    private fun loadPieChartData(pieChart: PieChart) {
        db.collection("reports").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Error loading reports: ${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                pieChart.clear()
                pieChart.centerText = "No reports yet"
                pieChart.invalidate()
                return@addSnapshotListener
            }

            var resolvedCount = 0f
            var unresolvedCount = 0f

            for (doc in snapshot.documents) {
                val status = doc.getString("status") ?: "Unresolved"
                if (status.equals("Resolved", ignoreCase = true) || status.equals("Completed", ignoreCase = true)) resolvedCount++
                else unresolvedCount++
            }

            val entries = listOf(
                PieEntry(resolvedCount, "Resolved"),
                PieEntry(unresolvedCount, "Unresolved")
            )

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = listOf("#4CAF50".toColorInt(), "#F44336".toColorInt())
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 14f
            dataSet.setDrawValues(true)

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.centerText = "Reports Overview"
            pieChart.setCenterTextSize(16f)
            pieChart.animateY(1000)

            val legend = pieChart.legend
            legend.isEnabled = true
            legend.textSize = 14f
            legend.formSize = 14f
            legend.form = Legend.LegendForm.CIRCLE
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)

            pieChart.invalidate()
        }
    }

    private fun loadRecentReports() {
        if (assignedBarangay == null || assignedBarangay == "Unknown") {
            Toast.makeText(requireContext(), "No assigned barangay found", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("reports")
            .whereEqualTo("barangay", assignedBarangay)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading recent reports", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                recentReports.clear()
                snapshot?.documents?.forEach { doc ->
                    val report = doc.toObject(Report::class.java)
                    report?.let { recentReports.add(it) }
                }
                recentAdapter.updateReports(recentReports)
            }
    }

    private fun onViewReport(report: Report) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Report Details")
            .setMessage("Category: ${report.category}\nDescription: ${report.description}\nStatus: ${report.status}")
            .setPositiveButton("OK", null)
            .show()
    }
}