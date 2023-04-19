package com.example.kotlintodo2.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.example.kotlintodo2.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }


}