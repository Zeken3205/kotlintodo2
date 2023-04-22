package com.example.kotlintodo2

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kotlintodo2.databinding.ActivityHomeBinding
import com.example.kotlintodo2.ui.AddTodoPopupFragment.Companion.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class Home : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.action_global_navigation_home)
                    }
                    true
                }
                R.id.nav_scheduler -> {
                    if (navController.currentDestination?.id != R.id.navigation_dashboard) {
                        navController.navigate(R.id.action_global_navigation_dashboard)
                    }
                    true
                }
                R.id.nav_pending -> {
                    if (navController.currentDestination?.id != R.id.pendingFragment) {
                        navController.navigate(R.id.action_global_pendingFragment)
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (navController.currentDestination?.id != R.id.profileFragment) {
                        navController.navigate(R.id.action_global_profileFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
