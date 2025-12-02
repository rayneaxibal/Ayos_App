package com.example.ayos  // Ensure this matches your package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ayos.models.Report

class RecentReportAdapter(
    private var reports: MutableList<Report>,
    private val onView: (Report) -> Unit
) : RecyclerView.Adapter<RecentReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardReport)
        val categoryText: TextView = view.findViewById(R.id.textCategory)
        val statusText: TextView = view.findViewById(R.id.textStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.categoryText.text = report.category
        holder.statusText.text = report.status
        holder.card.setOnClickListener { onView(report) }
    }

    override fun getItemCount() = reports.size

    fun updateReports(newReports: List<Report>) {
        reports = newReports.toMutableList()
        notifyDataSetChanged()
    }
}