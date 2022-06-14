package com.danielmijens.loginapp

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
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
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.danielmijens.loginapp.entidades.ControlVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
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
    private lateinit var youtubePlayerTracker : YouTubePlayerTracker
    private var shortAnimationDuration: Int = 10000000
    var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var botonAuxiliar : ImageButton
    private lateinit var botonAtras : ImageButton
    private lateinit var controlVideo : ControlVideo
    private var segundosActual = 0f

    lateinit var listener : OnFragmentListener

    // default position of image
    private var xDelta = 0
    private var yDelta = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentGrupoElegidoBinding.inflate(layoutInflater)
        controlVideo = ControlVideo()
        youTubePlayerView = binding.playerViewGrupo!!

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

        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.mostrarVideo?.isEnabled = true
                //player.play()
            }
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAtras = toolbar.rootView.findViewById<ImageButton>(R.id.botonAtras)


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
            listener.onVerInfoGrupo(grupoElegido,toolbar)
        }

        botonAtras.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.mostrarVideo?.setOnClickListener {
            if (binding.buscarYtLink?.query != null) {
                if (binding.playerViewGrupo?.visibility == View.GONE) {
                    toolbar.visibility = View.GONE
                    //binding.linearLayoutControles.visibility = View.VISIBLE
                    binding.mostrarVideo!!.setImageResource(R.mipmap.deslizar_arriba)
                    if (activity?.resources?.configuration?.orientation == ORIENTATION_LANDSCAPE ){
                        binding.linearLayoutEnviarMensajes.visibility = View.GONE
                        binding.playerViewGrupo?.visibility = View.VISIBLE
                    }else {

                        ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", -500f).apply {
                            this.duration = 0
                            start()
                        }.doOnEnd {
                            binding.playerViewGrupo?.visibility = View.VISIBLE
                            ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", 90f).apply {
                                this.duration = 500
                                start()
                            }
                        }
                    }

                } else {
                    binding.linearLayoutEnviarMensajes.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    //binding.linearLayoutControles.visibility = View.GONE
                    binding.mostrarVideo!!.setImageResource(R.mipmap.deslizar_abajo)
                    ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", -500f).apply {
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
                Log.d("controlVideo Contiene", controlVideo.toString())
                if (videoSearch != null && esYT(videoSearch)) {
                    controlVideo.videoElegido = videoSearch
                    cambiarVideoYT(controlVideo,videoSearch,0f,false)
                }else {
                    Toast.makeText(context,"No puede añadir un link vacio o que no sea de YT",Toast.LENGTH_SHORT).show()
                }
                videoYT()
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
        GlobalScope.launch (Dispatchers.IO) {
            controlVideo = Consultas.buscarControlVideoPorID(grupoElegido.idGrupo!!)
            withContext(Dispatchers.Main) {
                videoYT()
            }
        }

        youtubeFullscreen()

        GlobalScope.launch(Dispatchers.IO) {
            Consultas.usuarioOnline(grupoElegido, usuarioActual, true)
        }
        if(grupoElegido.creador!=usuarioActual.email) {
            notificarAlModificarVideo()
        }else {
            notificarAlEntrar()
        }

        botonAuxiliar.setBackgroundResource(R.drawable.info_grupo)
        botonAuxiliar.visibility = View.VISIBLE

        botonAtras.setBackgroundResource(R.mipmap.boton_atras)
        botonAtras.visibility = View.VISIBLE
    }

    override fun onStop() {
        GlobalScope.launch (Dispatchers.IO) {
            Consultas.usuarioOnline(grupoElegido,usuarioActual,false)
            Consultas.actualizarVideoIniciado(controlVideo,false)
        }
        toolbar.visibility = View.VISIBLE
        Log.d("Salgo","Salgo del fragment")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("Salgo","Salgo del grupo")
        GlobalScope.launch (Dispatchers.IO) {
            Consultas.usuarioOnline(grupoElegido,usuarioActual,false)
            Consultas.actualizarVideoIniciado(controlVideo,false)
            controlVideo.videoIniciado = false
            grupoElegido.videoIniciado = false
        }
        botonAtras.visibility = View.GONE
        toolbar.visibility = View.VISIBLE
        super.onDestroy()
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
         if (controlVideo.videoElegido != null && esYT(controlVideo.videoElegido!!)) {
             youtubePlayerTracker = YouTubePlayerTracker()
             //youtubeSeekListener()

             if (youTubePlayerView != null) {
                 lifecycle.addObserver(youTubePlayerView)
             }

             youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                 override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                     //if your url is something like this -> https://www.youtube.com/watch?v=EzyXVfyx7CU
                     val urlToLoad = controlVideo.videoElegido!!
                     val url = sacarUrlYT(urlToLoad)
                     var segundos = controlVideo.videoSegundos
                     if (controlVideo.videoIniciado != true || segundos == null || segundos == 0f) {
                         youTubePlayer.cueVideo(url[1], 0f)
                     }else {
                         youTubePlayer.loadVideo(url[1], segundos)
                     }
                     youTubePlayerView.getPlayerUiController().showYouTubeButton(false)
                     if (grupoElegido.creador!=usuarioActual.email) {
                         youTubePlayerView.getPlayerUiController().showPlayPauseButton(false)
                         youTubePlayerView.getPlayerUiController().showSeekBar(false)
                     }else {
                         youTubePlayerView.getPlayerUiController().showPlayPauseButton(true)
                         youTubePlayerView.getPlayerUiController().showSeekBar(true)
                     }
                 }

                 override fun onStateChange(
                     youTubePlayer: YouTubePlayer,
                     state: PlayerConstants.PlayerState
                 ) {

                     if (grupoElegido.creador==usuarioActual.email) {
                         var segundos = 0f

                         GlobalScope.launch(Dispatchers.IO) {
                             if (state.equals(PlayerConstants.PlayerState.PAUSED)) {
                                 segundos = youtubePlayerTracker.currentSecond
                                 Log.d("Se ha pausado Dani"," Pausadoooo " + segundos.toString())
                                 Consultas.actualizarSegundos(controlVideo,segundos)
                                 Consultas.actualizarVideoIniciado(controlVideo,false)
                             }else if (state.equals(PlayerConstants.PlayerState.PLAYING)) {
                                 segundos = youtubePlayerTracker.currentSecond
                                 Log.d("Esta playing Dani"," Playiiiing " + segundos.toString())
                                 Consultas.actualizarSegundos(controlVideo,segundos) //Cuidado DANI
                                 Consultas.actualizarVideoIniciado(controlVideo,true)
                             }
                         }
                     }

                     super.onStateChange(youTubePlayer, state)
                 }

                 @SuppressLint("LongLogTag")
                 override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                     super.onCurrentSecond(youTubePlayer, second)
                     if (grupoElegido.creador==usuarioActual.email) {

                         var comprobacion =  youtubePlayerTracker.currentSecond-segundosActual
                         Log.d("Varia comprobacion Dan", comprobacion.toString())

                         if (comprobacion < 0 || comprobacion>= 1) {
                             GlobalScope.launch(Dispatchers.IO) {
                                 Consultas.actualizarSegundos(controlVideo,second)
                                 Log.d("Varia segundosActual Dani", segundosActual.toString())
                                 Log.d("Varia second Dani", second.toString())
                                 Log.d("Varia current", youtubePlayerTracker.currentSecond.toString())
                             }
                         }
                         segundosActual = second
                     }
                 }
             })
             youTubePlayerView.addYouTubePlayerListener(youtubePlayerTracker)

         }
    }

    fun cambiarVideoYT (controlVideoCambiar: ControlVideo, urlVideoYT: String, segundos: Float?, iniciado: Boolean?) {
        var iniciarListener = false
        if (!controlVideoCambiar.videoElegido.isNullOrEmpty()) {
            iniciarListener = true
        }
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                val url = sacarUrlYT(urlVideoYT)
                var todoBien = false
                if (url.isNullOrEmpty()||url.size<=0) {
                    if (context != null && usuarioActual.email==grupoElegido.creador) Toast.makeText(context,"Enlace no valido", Toast.LENGTH_SHORT).show()
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
                if (todoBien && usuarioActual.email==grupoElegido.creador) {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (segundos==null) {
                            Consultas.actualizarSegundos(controlVideoCambiar,0f)
                            Consultas.actualizarVideoElegido(controlVideoCambiar,urlVideoYT)
                            }else {
                            Consultas.actualizarSegundos(controlVideoCambiar,segundos)
                            Consultas.actualizarVideoElegido(controlVideoCambiar,urlVideoYT)
                        }

                    }
                }
                if (iniciarListener) {
                    videoYT()
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

    fun esYT(url: String): Boolean {
        var urlCorrecta = false
        if (url.contains("youtube.com/watch?v=")) {
            urlCorrecta = true
        }else if (url.contains("be/")) {
            urlCorrecta = true
        }
        return urlCorrecta
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notificarAlModificarVideo() {
        youtubePlayerTracker = YouTubePlayerTracker()
        mFirestore.collection("ControlVideos").whereEqualTo("idGrupo",grupoElegido.idGrupo)
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
                                var grupoControlVideo = cambioVideo.document.toObject(ControlVideo::class.java)
                            }else {
                                var grupoControlVideo = cambioVideo.document.toObject(ControlVideo::class.java)
                                Log.d("test grupoCambioVideo",grupoControlVideo.toString())
                                cambiarVideoYT(controlVideo
                                    ,grupoControlVideo.videoElegido.toString()
                                    ,grupoControlVideo.videoSegundos,
                                    grupoControlVideo.videoIniciado)
                            }

                        }
                    }
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notificarAlEntrar() {
        youtubePlayerTracker = YouTubePlayerTracker()
        mFirestore.collection("Grupos").whereEqualTo("idGrupo",grupoElegido.idGrupo)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("LongLogTag")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null ) {
                        Log.e("Firestore error video", error.message.toString())
                        return
                    }
                    for (interaccionUsuario in value?.documentChanges!!) {
                        if (interaccionUsuario.type == DocumentChange.Type.MODIFIED) {
                            var interaccionVideo = interaccionUsuario.document.toObject(ControlVideo::class.java)
                            if (view?.context != null) Toast.makeText(view?.context,"Ha entrado un usuario",Toast.LENGTH_SHORT).show()
                            Log.d("interaccionUsuario videoIniciado",interaccionVideo.videoIniciado.toString())
                            Log.d("interaccionUsuario videoSegundos",interaccionVideo.videoSegundos.toString())
                            if (interaccionVideo.videoSegundos!= null && interaccionVideo.videoIniciado!=null) {
                                //var seg = youtubePlayerTracker.currentSecond
                                var seg = interaccionVideo.videoSegundos
                                GlobalScope.launch (Dispatchers.IO){
                                    Log.d("interaccionUsuario youtubePlayerTracker.currentSecond",youtubePlayerTracker.currentSecond.toString())
                                    Consultas.actualizarSegundos(controlVideo,seg!!)

                                }
                            }
                        }
                    }
                }
            })
    }

    fun youtubeFullscreen() {
        youTubePlayerView.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
            if (youTubePlayerView.isFullScreen()) {
                ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", 90f).apply {
                    this.duration = 500
                    start()
                }
                binding.linearLayoutControles.visibility = View.VISIBLE
                binding.linearLayoutEnviarMensajes.visibility = View.VISIBLE
                binding.mensajesRecyclerView.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
                youTubePlayerView.exitFullScreen()
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                /*activity?.window?.setFlags(
                    WindowManager.LayoutParams.,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)*/
                showSystemUI()
            }else {
                Log.d("Full", "Pantalla completa")
                ObjectAnimator.ofFloat(binding.playerViewGrupo, "translationY", 0f).apply {
                    this.duration = 500
                    start()
                }
                youTubePlayerView.enterFullScreen()
                binding.linearLayoutControles.visibility = View.GONE
                binding.linearLayoutEnviarMensajes.visibility = View.GONE
                binding.mensajesRecyclerView.visibility = View.GONE
                toolbar.visibility = View.GONE
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                /*activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)*/
                hideSystemUI()
            }
        })
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity!!.window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
    }

    private fun showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity!!.window.insetsController?.show(WindowInsets.Type.statusBars())
        }
    }

}