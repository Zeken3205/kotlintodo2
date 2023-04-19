package com.example.kotlintodo2

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kotlintodo2.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Home : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_scheduler -> {
                    if (navController.currentDestination?.id != R.id.navigation_dashboard) {
                        if (navController.currentDestination?.id != R.id.pendingFragment) {
                            navController.navigate(R.id.action_navigation_home_to_navigation_dashboard)
                        }
                        else if (navController.currentDestination?.id != R.id.navigation_home) {
                            navController.navigate(R.id.action_pendingFragment_to_navigation_dashboard)
                        }
                    }
                    true
                }
                R.id.nav_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        if (navController.currentDestination?.id != R.id.pendingFragment) {
                            navController.navigate(R.id.action_navigation_dashboard_to_navigation_home)
                        }
                        else if (navController.currentDestination?.id != R.id.navigation_dashboard) {
                            navController.navigate(R.id.action_pendingFragment_to_navigation_home)
                        }
                    }
                    true
                }
                R.id.nav_pending -> {
                    if (navController.currentDestination?.id != R.id.pendingFragment) {
                        if (navController.currentDestination?.id != R.id.nav_home) {
                            navController.navigate(R.id.action_navigation_dashboard_to_pendingFragment)
                        }
                        else if (navController.currentDestination?.id != R.id.navigation_dashboard) {
                            navController.navigate(R.id.action_navigation_home_to_pendingFragment)
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }
}