package com.danielmijens.loginapp.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielmijens.loginapp.databinding.FragmentVerVideoBinding

import com.danielmijens.loginapp.entidades.UsuarioActual
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import android.R
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VerVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerVideoFragment(val usuarioActual: UsuarioActual) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentVerVideoBinding
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var youtubePlayerTracker : YouTubePlayerTracker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVerVideoBinding.inflate(layoutInflater)
        binding.listaVideosYoutube?.webViewClient = WebViewClient()
        binding.listaVideosYoutube?.loadUrl("https://www.youtube.com/watch?v=ha0-qytMD9k")
        youTubePlayerView = binding.youtubePlayerView!!
        youtubePlayerTracker = YouTubePlayerTracker()
        binding.recyclerVideosYT?.layoutManager = LinearLayoutManager(context)

        if (youTubePlayerView != null) {
            lifecycle.addObserver(youTubePlayerView)
        }

        YoutubeCon


        youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                //if your url is something like this -> https://www.youtube.com/watch?v=EzyXVfyx7CU
                val urlToLoad = "https://www.youtube.com/watch?v=ha0-qytMD9k"
                val url = urlToLoad.split("watch?v=").toTypedArray()
                youTubePlayer.loadVideo(url[1], 0f)
                //if your url is something like this -> EzyXVfyx7CU
                val videoId = "ha0-qytMD9k"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
        youTubePlayerView.addYouTubePlayerListener(youtubePlayerTracker)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button?.setOnClickListener {
            if (youtubePlayerTracker.currentSecond<=60) {
                Toast.makeText(context,youtubePlayerTracker.currentSecond.toString(),Toast.LENGTH_SHORT).show()
            }else {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


}