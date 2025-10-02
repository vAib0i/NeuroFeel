package com.example.neurofeel

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.IOException

class MusicFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playPauseButton: ImageButton
    private var isPrepared = false
    private var currentPosition = 0
    private var isLoading = false

    private val soundUrls = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)

        playPauseButton = view.findViewById(R.id.playPauseButton)

        playPauseButton.setOnClickListener {
            Log.d("MusicFragment", "Play button clicked")
            when {
                mediaPlayer == null -> {
                    playPauseButton.isEnabled = false
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
                    playRandomSound()
                }
                mediaPlayer!!.isPlaying -> {
                    currentPosition = mediaPlayer!!.currentPosition
                    mediaPlayer!!.pause()
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                }
                isPrepared -> {
                    mediaPlayer!!.seekTo(currentPosition)
                    mediaPlayer!!.start()
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
                }
                else -> {
                    Toast.makeText(requireContext(), "Please wait... still loading.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun playRandomSound() {
        val randomSoundUrl = soundUrls.random()
        Log.d("MusicFragment", "Attempting to play: $randomSoundUrl")

        isPrepared = false
        isLoading = true

        try {
            mediaPlayer?.release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(randomSoundUrl)

                setOnPreparedListener {
                    isPrepared = true
                    isLoading = false
                    start()
                    playPauseButton.isEnabled = true
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
                    Toast.makeText(requireContext(), "Now playing", Toast.LENGTH_SHORT).show()
                }

                setOnCompletionListener {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                    Toast.makeText(requireContext(), "Playback completed", Toast.LENGTH_SHORT).show()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayer", "Error: what=$what extra=$extra")
                    Toast.makeText(requireContext(), "Playback error occurred.", Toast.LENGTH_SHORT).show()
                    playPauseButton.isEnabled = true
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                    true
                }

                prepareAsync()
            }
        } catch (e: IOException) {
            Log.e("MediaPlayer", "IOException: ${e.message}")
            Toast.makeText(requireContext(), "Error: Couldn't load audio.", Toast.LENGTH_SHORT).show()
            playPauseButton.isEnabled = true
            playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Exception: ${e.message}")
            Toast.makeText(requireContext(), "Unexpected error.", Toast.LENGTH_SHORT).show()
            playPauseButton.isEnabled = true
            playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        currentPosition = mediaPlayer?.currentPosition ?: 0
    }

    override fun onResume() {
        super.onResume()
        if (isPrepared && currentPosition > 0) {
            mediaPlayer?.seekTo(currentPosition)
            mediaPlayer?.start()
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }
}
