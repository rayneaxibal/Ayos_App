package com.example.ayos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class LguEditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lgu_edit_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lgu_edit_profile_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigate to LguDashboardActivity instead of loading fragment
                    val intent = android.content.Intent(this, LguDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.report -> {
                    // Navigate to LguReportStatusActivity
                    val intent = android.content.Intent(this, LguReportStatusActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.profile -> {
                    // Navigate to LguProfileActivity
                    val intent = android.content.Intent(this, LguProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Load the edit profile fragment by default
        if (savedInstanceState == null) {
            loadFragment(LguEditProfileFragment())
        }

        // Highlight the Profile tab since we're in edit profile
        bottomNav.post {
            bottomNav.selectedItemId = R.id.profile
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}