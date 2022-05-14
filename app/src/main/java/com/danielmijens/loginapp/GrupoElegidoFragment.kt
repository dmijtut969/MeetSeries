package com.danielmijens.loginapp

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.adapters.AdapterGrupoElegido
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.Mensaje
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.SparseArray
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.loader.content.AsyncTaskLoader
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrupoElegidoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GrupoElegidoFragment(
    var usuarioActual: UsuarioActual,
    val grupoElegido: Grupo,
    var toolbar: androidx.appcompat.widget.Toolbar
) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentGrupoElegidoBinding
    private lateinit var adapter : AdapterGrupoElegido
    private var listaMensajes = mutableListOf<Mensaje>()
    private lateinit var youTubePlayerView : YouTubePlayerView
    private var shortAnimationDuration: Int = 10000000
    var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var botonAuxiliar : ImageButton
    lateinit var listener : OnFragmentListener

    // default position of image
    private var xDelta = 0
    private var yDelta = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentGrupoElegidoBinding.inflate(layoutInflater)
        var recyclerView = binding.mensajesRecyclerView

        var linearLayout = LinearLayoutManager(context)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        linearLayout.stackFromEnd = false
        linearLayout.reverseLayout = false
        recyclerView.layoutManager = linearLayout
        //recyclerView.setHasFixedSize(true)
        Log.d("grupoElegido test ", grupoElegido.toString())
        adapter = AdapterGrupoElegido(binding,listaMensajes,usuarioActual,this)

        recyclerView.adapter = adapter
        leerMensajesListener(recyclerView)

        videoYT()

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.mostrarVideo?.isEnabled = true
                //player.play()
            }
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAuxiliar.setBackgroundResource(R.drawable.info_grupo)
        botonAuxiliar.visibility = View.VISIBLE

        //Para mover el videoview
        //binding.constraintLayoutVideo?.setOnTouchListener(onTouchListener())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (grupoElegido.creador.equals(usuarioActual.email)) {
            binding.buscarYtLink?.visibility  = View.VISIBLE
        }else {
            binding.buscarYtLink?.visibility  = View.GONE
        }
        binding.imageButton.setOnClickListener {
                var mensajeNuevo = binding.editTextMensajeNuevo.text.trim().toString()
                GlobalScope.launch(Dispatchers.IO) {
                    if (mensajeNuevo != "") {
                        Consultas.enviarMensajeAGrupo(mensajeNuevo,grupoElegido,usuarioActual)
                    }
                    withContext(Dispatchers.Main) {
                        binding.editTextMensajeNuevo.setText("")
                    }
                }
        }
        toolbar.setTitle(grupoElegido.nombreGrupo)

        botonAuxiliar.setOnClickListener {
            Toast.makeText(context,"Holaaaa",Toast.LENGTH_SHORT).show()
            listener.onVerInfoGrupo(grupoElegido,toolbar)
        }
        binding.mostrarVideo?.setOnClickListener {
            if (binding.buscarYtLink?.query != null) {
                if (binding.playerViewGrupo?.visibility == View.GONE) {
                    binding.mostrarVideo!!.setImageResource(R.mipmap.deslizar_arriba)
                    ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", -550f).apply {
                        this.duration = 0
                        start()
                    }.doOnEnd {
                        binding.playerViewGrupo?.visibility = View.VISIBLE
                        ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", 90f).apply {
                            this.duration = 500
                            start()
                        }
                    }
                } else {
                    binding.mostrarVideo!!.setImageResource(R.mipmap.deslizar_abajo)
                    ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", -550f).apply {
                        this.duration = 500
                        start()
                    }.doOnEnd {
                        binding.playerViewGrupo?.visibility = View.GONE
                    }
                }
            }
        }

        binding.buscarYtLink?.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.buscarYtLink?.clearFocus()
                var videoSearch = binding.buscarYtLink?.query.toString()
                //if (videoSearch.contains("youtube")){
                    Log.d("searchView Contiene", "Es youtube")
                    if (grupoElegido.videoElegido != null) {
                        cambiarVideoYT(grupoElegido,videoSearch,0f,false)
                    }
                //}else {
                    if (query != null) {
                        Log.d("searchView No Contiene",query)
                    }
               // }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    @SuppressLint("LongLogTag")
    override fun onStart() {
        super.onStart()
        videoYT()
        notificarAlModificarVideo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun leerMensajesListener(recyclerView: RecyclerView) {
        listaMensajes.clear()
        mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            .collection("Mensajes").orderBy("hora",Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null ) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }
                    for (cambioMensaje in value?.documentChanges!!) {
                        if (cambioMensaje.type == DocumentChange.Type.ADDED) {
                            listaMensajes.add(cambioMensaje.document.toObject(Mensaje::class.java))
                            adapter.notifyItemInserted(listaMensajes.size)
                        }
                    }
                    Log.d("Se ha cambiado", "Ha habido un cambio en los mensajes")
                        recyclerView.smoothScrollToPosition(listaMensajes.size)
                    }
            })

            recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }

     fun videoYT() {
         if (grupoElegido.videoElegido != null) {
             youTubePlayerView = binding.playerViewGrupo!!
             var youtubePlayerTracker = YouTubePlayerTracker()
             //binding.recyclerVideosYT?.layoutManager = LinearLayoutManager(context)

             if (youTubePlayerView != null) {
                 lifecycle.addObserver(youTubePlayerView)
             }

             youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                 override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                     //if your url is something like this -> https://www.youtube.com/watch?v=EzyXVfyx7CU
                     val urlToLoad = grupoElegido.videoElegido
                     val url = sacarUrlYT(urlToLoad)
                     var segundos = grupoElegido.videoSegundos
                     if (grupoElegido.videoIniciado != true || segundos == null || segundos == 0f) {
                         youTubePlayer.cueVideo(url[1], 0f)
                     }else {
                         youTubePlayer.loadVideo(url[1], segundos)
                     }
                     if (grupoElegido.creador!=usuarioActual.email) {
                         youTubePlayerView.getPlayerUiController().showPlayPauseButton(false)
                     }else {
                         youTubePlayerView.getPlayerUiController().showPlayPauseButton(true)
                     }
                 }

                 override fun onStateChange(
                     youTubePlayer: YouTubePlayer,
                     state: PlayerConstants.PlayerState
                 ) {
                     if (state.equals(PlayerConstants.PlayerState.PAUSED)) {
                         Log.d("Se ha pausado Dani"," Pausadoooo")
                     }else if (state.equals(PlayerConstants.PlayerState.PLAYING)) {
                         Log.d("Esta playing Dani"," Playiiiing")
                     }
                     super.onStateChange(youTubePlayer, state)
                 }
             })
             youTubePlayerView.addYouTubePlayerListener(youtubePlayerTracker)
         }
    }

    fun cambiarVideoYT (grupoACambiar : Grupo,urlVideoYT : String, segundos : Float?,iniciado : Boolean?) {
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                val url = sacarUrlYT(urlVideoYT)
                var todoBien = false
                if (url.isNullOrEmpty()||url.size<=0) {
                    Toast.makeText(context,"Enlace no valido", Toast.LENGTH_SHORT).show()
                }else if (iniciado == true) {
                    if (segundos==null) {
                       youTubePlayer.loadVideo(url[1], 0f)
                    }else {
                        youTubePlayer.loadVideo(url[1], segundos)
                    }
                    todoBien = true
                }else {
                    youTubePlayer.cueVideo(url[1], 0f)
                    todoBien = true
                }
                if (todoBien) {
                    GlobalScope.launch(Dispatchers.IO) {
                        Consultas.actualizarVideoElegido(grupoACambiar,urlVideoYT)
                    }
                }
            }
        })
    }

    fun sacarUrlYT (url : String): Array<String> {
        var urlReturn = emptyArray<String>()
        if (url.contains("youtube.com/watch?v=")) {
            urlReturn = url.split("youtube.com/watch?v=").toTypedArray()
        }else if (url.contains("be/")) {
            urlReturn = url.split("be/").toTypedArray()
        }
        return urlReturn
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notificarAlModificarVideo() {
        mFirestore.collection("Grupos").whereEqualTo("idGrupo",grupoElegido.idGrupo)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("LongLogTag")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null ) {
                        Log.e("Firestore error video", error.message.toString())
                        return
                    }
                    for (cambioVideo in value?.documentChanges!!) {
                        if (cambioVideo.type == DocumentChange.Type.MODIFIED) {
                            if (grupoElegido.creador == usuarioActual.email) {
                                Toast.makeText(context,"Se esta cambiando a los demas",Toast.LENGTH_SHORT).show()
                                var grupoCambioVideo = cambioVideo.document.toObject(Grupo::class.java)
                            }else {
                                var grupoCambioVideo = cambioVideo.document.toObject(Grupo::class.java)
                                Log.d("test grupoCambioVideo",grupoCambioVideo.toString())
                                cambiarVideoYT(grupoCambioVideo
                                    ,grupoCambioVideo.videoElegido.toString()
                                    ,grupoCambioVideo.videoSegundos,
                                    grupoCambioVideo.videoIniciado)
                            }

                        }
                    }
                }
            })

    }

}