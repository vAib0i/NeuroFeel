package com.example.neurofeel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sadCard = view.findViewById<CardView>(R.id.card_sad)
        val angryCard = view.findViewById<CardView>(R.id.card_angry)
        val neutralCard = view.findViewById<CardView>(R.id.card_neutral)

        sadCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MusicFragment())
                .addToBackStack(null)
                .commit()
        }

        angryCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BreathingFragment())
                .addToBackStack(null)
                .commit()
        }

        neutralCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FactsFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
