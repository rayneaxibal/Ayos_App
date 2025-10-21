package com.example.ayos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class ReportAdapter(
    private val allReports: List<Report>,
    private val onItemClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var filteredReports: List<Report> = allReports

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusText: TextView = itemView.findViewById(R.id.card_status)
        val titleText: TextView = itemView.findViewById(R.id.card_title)
        val dateText: TextView = itemView.findViewById(R.id.card_date)
        val viewButton: ImageButton = itemView.findViewById(R.id.btn_view)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = filteredReports[position]

        holder.statusText.text = report.status
        holder.titleText.text = report.title
        holder.dateText.text = report.date

        holder.statusText.setBackgroundResource(R.drawable.active)
        holder.statusText.setTextColor(Color.WHITE)

        holder.viewButton.setOnClickListener { onItemClick(report) }
        holder.deleteButton.setOnClickListener { /* Implement delete if needed */ }
    }

    override fun getItemCount(): Int = filteredReports.size

    // Filter reports by status
    fun filterByStatus(status: String) {
        filteredReports = if (status == "All") allReports
        else allReports.filter { it.status == status }
        notifyDataSetChanged()
    }
}