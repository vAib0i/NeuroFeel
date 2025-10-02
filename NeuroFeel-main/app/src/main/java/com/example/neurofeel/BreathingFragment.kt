package com.example.neurofeel

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView

class BreathingFragment : Fragment() {

    private lateinit var timerTextView: TextView
    private lateinit var lottieView: LottieAnimationView
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_breathing, container, false)

        // Get references
        timerTextView = view.findViewById(R.id.timerTextView)
        lottieView = view.findViewById(R.id.lottieView)

        // Start timer
        startTimer(2 * 60 * 1000L) // 2 minutes

        return view
    }

    private fun startTimer(durationInMillis: Long) {
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerTextView.text = "Done"
                lottieView.pauseAnimation()
            }
        }
        countDownTimer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
    }
}
