package com.example.kotlintodo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.kotlintodo2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

    }

    override fun onStart() {
        super.onStart()
        val MaintoSplash=Intent(this,SplashActivity::class.java)
        val MaintoHome=Intent(this,Home::class.java)
        auth= FirebaseAuth.getInstance()
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if(auth.currentUser!=null){
                startActivity(MaintoHome)
            }else{
                startActivity(MaintoSplash)
            }
        },3000)
    }
}