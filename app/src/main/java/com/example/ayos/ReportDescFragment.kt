package com.example.ayos

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class ReportDescFragment : Fragment(R.layout.fragment_report_desc) {

    private lateinit var backButton: ImageButton
    private lateinit var btnCont: ImageButton
    private lateinit var multiLineText: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        btnCont = view.findViewById(R.id.btnCont)
        multiLineText = view.findViewById(R.id.multiLineText)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCont.setOnClickListener {
            val attachFragment = ReportAttachFragment().apply {
                arguments = Bundle().apply {
                    putString("description", multiLineText.text.toString())
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, attachFragment)
                .addToBackStack("ReportAttach")
                .commit()
        }
    }
}