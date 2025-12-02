package com.example.ayos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ReportDescFragment : Fragment(R.layout.fragment_report_desc) {

    private lateinit var backButton: ImageButton
    private lateinit var btnCont: ImageButton
    private lateinit var multiLineText: EditText
    private lateinit var charCounter: TextView
    private lateinit var errorText: TextView

    private lateinit var viewModel: ReportViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ReportViewModel::class.java)

        backButton = view.findViewById(R.id.backButton)
        btnCont = view.findViewById(R.id.btnCont)
        multiLineText = view.findViewById(R.id.multiLineText)
        charCounter = view.findViewById(R.id.charCounter)  // Initialize counter
        errorText = view.findViewById(R.id.errorText)      // Initialize error text

        multiLineText.setText(viewModel.description)

        multiLineText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                charCounter.text = "$length/500"
                if (length > 500) {
                    multiLineText.setText(s?.subSequence(0, 500))
                    multiLineText.setSelection(500)
                }
                if (errorText.visibility == View.VISIBLE) {
                    errorText.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCont.setOnClickListener {
            val description = multiLineText.text.toString().trim()
            if (description.isEmpty()) {
                errorText.text = "Please enter a description"
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }
            errorText.visibility = View.GONE
            viewModel.description = description

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ReportAttachFragment())
                .addToBackStack("ReportAttach")
                .commit()
        }
    }
}