package com.example.ayos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ayos.models.Report

class LguReportAdapter(
    private var reports: MutableList<Report>,
    private val onView: (Report) -> Unit,
    private val onUpdate: (Report) -> Unit,
    private val onDelete: (Report) -> Unit
) : RecyclerView.Adapter<LguReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryText: TextView = view.findViewById(R.id.textCategory)
        val statusText: TextView = view.findViewById(R.id.textStatus)
        val dateText: TextView = view.findViewById(R.id.card_date)
        val viewButton: ImageButton = view.findViewById(R.id.btnView)
        val updateButton: ImageButton = view.findViewById(R.id.btnUpdate)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lgu_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.categoryText.text = report.category
        holder.statusText.text = report.status
        holder.dateText.text = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date(report.timestamp))
        holder.viewButton.setOnClickListener { onView(report) }
        holder.updateButton.setOnClickListener { onUpdate(report) }
        holder.deleteButton.setOnClickListener { onDelete(report) }
    }

    override fun getItemCount() = reports.size

    fun updateReports(newReports: List<Report>) {
        reports = newReports.toMutableList()
        notifyDataSetChanged()
    }

    fun filterByStatus(status: String) {
        val filtered = if (status == "All") reports else reports.filter { it.status == status }
        reports.clear()
        reports.addAll(filtered)
        notifyDataSetChanged()
    }
}