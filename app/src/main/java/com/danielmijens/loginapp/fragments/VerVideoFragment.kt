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
    private lateinit var player : ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVerVideoBinding.inflate(layoutInflater)

        player = ExoPlayer.Builder(context!!).build()
        binding.playerViewGrupo?.player = player
        var videoUri = Uri.parse("https://s-delivery33.mxdcontent.net/v/b7238e6b97aee4bfd3c05bf68579ad6f.mp4?s=pPcePFG6y7CO2lnH0k3Nog&e=1647897243&_t=1647882576")
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)
// Set the media item to be played.
// Set the media item to be played.
        player.setMediaItem(mediaItem)
// Prepare the player.
// Prepare the player.
        player.prepare()
// Start the playback.
// Start the playback.
        player.play()



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button?.setOnClickListener {
            player.seekTo(240000)

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