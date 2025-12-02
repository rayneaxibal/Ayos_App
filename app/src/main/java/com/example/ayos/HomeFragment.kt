package com.example.ayos

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.components.Legend
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.graphics.toColorInt

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        val reportButton = view.findViewById<ImageButton>(R.id.btnReport)

        reportButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ReportCategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        val prefs = requireActivity().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userPhone = prefs.getString("loggedInPhone", null) ?: return  // Exit if no session

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
                if (status.equals("Resolved", ignoreCase = true)) resolvedCount++
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
            pieChart.centerText = "Reports"
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
}