package com.example.kotlintodo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlintodo2.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
    }
    override fun onStart() {
        super.onStart()
        registerEvents()
    }

    private fun registerEvents() {
        val SplashtoSignUp= Intent(this,SignupActivity::class.java)
        val SplashtoLogin= Intent(this,LoginActivity::class.java)
        binding.Loginbuttonhome.setOnClickListener {
            startActivity(SplashtoLogin)
        }
        binding.elseSignup.setOnClickListener {
            startActivity(SplashtoSignUp)
        }
    }
}