package com.example.kotlintodo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodo2.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
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
    private fun registerEvents()
    {
        val SignuptoLoginIntent=Intent(this,LoginActivity::class.java)
        val SignuptoHomeIntent=Intent(this,Home::class.java)
        val context=this
        binding.LoginTrigger.setOnClickListener {
            startActivity(SignuptoLoginIntent)
        }
        binding.Signupbutton.setOnClickListener {
            val username=binding.GetName.text.toString().trim()
            val email=binding.GetEmailAddress.text.toString().trim()
            val password=binding.GetPassword.text.toString().trim()

            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                    OnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context,"Registered Done", Toast.LENGTH_SHORT).show()
                            startActivity(SignuptoHomeIntent)
                        }else{
                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}