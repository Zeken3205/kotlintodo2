package com.example.kotlintodo2.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlintodo2.SplashActivity
import com.example.kotlintodo2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerEvents()
        auth= FirebaseAuth.getInstance()
    }
    fun registerEvents()
    {
        binding.LogOutbtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val profiletoSplash= Intent(requireContext(), SplashActivity::class.java)
            startActivity(profiletoSplash)

            Toast.makeText(context,"Log Out Successful",Toast.LENGTH_SHORT)
        }
    }

}