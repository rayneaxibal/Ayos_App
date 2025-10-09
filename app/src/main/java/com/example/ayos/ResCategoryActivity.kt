package com.example.ayos

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class ResCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_res_category)

        val searchView = findViewById<SearchView>(R.id.searchView)

        // Get the search text field inside SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        // Apply colors
        searchEditText.setHintTextColor(android.graphics.Color.parseColor("#3C693D"))
        searchEditText.setTextColor(android.graphics.Color.parseColor("#3C693D"))
    }
}
