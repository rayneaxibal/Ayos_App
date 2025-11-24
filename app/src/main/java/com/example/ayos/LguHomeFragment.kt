package com.example.ayos

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class LguHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lgu_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        // Example data - you can replace this with real data from your backend
        val resolved = 70f
        val unresolved = 30f

        val entries = listOf(
            PieEntry(resolved, "Resolved"),
            PieEntry(unresolved, "Unresolved")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
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

        // Update the text views with dynamic data
        val totalPendingReports = view.findViewById<android.widget.TextView>(R.id.totalPendingReports)
        val resolvedTextView = view.findViewById<android.widget.TextView>(R.id.resolvedTextView)
        val unresolvedTextView = view.findViewById<android.widget.TextView>(R.id.unresolvedTextView)

        // Example data - replace with actual counts from your database
        val pendingCount = 1
        val resolvedCount = 70
        val unresolvedCount = 30

        totalPendingReports.text = "$pendingCount Pending Report${if (pendingCount != 1) "s" else ""}"
        resolvedTextView.text = "$resolvedCount Resolved Issues"
        unresolvedTextView.text = "$unresolvedCount Unresolved Issues"
    }
}