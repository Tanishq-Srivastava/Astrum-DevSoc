package com.example.astrum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val b2 = view.findViewById<View>(R.id.mapButton) as Button
        b2.setOnClickListener {
            Toast.makeText(context, "Loading monitoring system", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MapFragment::class.java)
            startActivity(intent)
        }
        return view
    }
}