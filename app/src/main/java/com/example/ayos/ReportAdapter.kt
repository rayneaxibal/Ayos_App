package com.example.ayos

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ayos.models.Report

class ReportAdapter(
    private var allReports: MutableList<Report>,
    private val onViewClick: (Report) -> Unit,
    private val onEditClick: (Report) -> Unit,  // Added for Update
    private val onDeleteClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var filteredReports: MutableList<Report> = allReports.toMutableList()

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusText: TextView = itemView.findViewById(R.id.card_status)
        val titleText: TextView = itemView.findViewById(R.id.card_title)
        val dateText: TextView = itemView.findViewById(R.id.card_date)
        val viewButton: ImageButton = itemView.findViewById(R.id.btn_view)
        val editButton: ImageButton = itemView.findViewById(R.id.btn_edit)
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
        holder.titleText.text = report.category

        val dateStr = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        ).format(java.util.Date(report.timestamp))
        holder.dateText.text = dateStr

        holder.statusText.setBackgroundResource(
            when (report.status) {
                "Submitted" -> R.drawable.active
                "In Progress" -> R.drawable.inactive
                "Under Review" -> R.drawable.inactive
                "Completed" -> R.drawable.active
                else -> R.drawable.inactive
            }
        )
        holder.statusText.setTextColor(Color.WHITE)

        holder.viewButton.setOnClickListener { onViewClick(report) }
        holder.editButton.setOnClickListener { onEditClick(report) }  // Added
        holder.deleteButton.setOnClickListener { onDeleteClick(report) }
    }

    override fun getItemCount(): Int = filteredReports.size

    fun filterByStatus(status: String) {
        filteredReports = if (status == "All") {
            allReports.toMutableList()
        } else {
            allReports.filter { it.status == status }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateReports(newList: List<Report>) {
        allReports = newList.toMutableList()
        filteredReports = allReports.toMutableList()
        notifyDataSetChanged()
    }
}