package com.example.kotlintodo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodo2.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

    }

    override fun onStart() {
        super.onStart()
        init()
        registerEvents()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents() {
        val LogintoHomeIntent = Intent(this, Home::class.java)
        val LogintoSignUpIntent = Intent(this, SignupActivity::class.java)
        val context=this

        binding.Signuptrigger.setOnClickListener {

            startActivity(LogintoSignUpIntent)
        }

        binding.Loginbutton.setOnClickListener {
            val email=binding.GetLoginEmailAddress.text.toString().trim()
            val password=binding.GetLoginPassword.text.toString().trim()

            if(email.isNotEmpty()&&password.isNotEmpty())
            {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            startActivity(LogintoHomeIntent)
                        }else{
                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

}