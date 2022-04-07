package com.danielmijens.loginapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.danielmijens.loginapp.firebase.Storage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrupoElegidoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GrupoElegidoFragment(var usuarioActual: UsuarioActual,val grupoElegido : Grupo) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentGrupoElegidoBinding
    private lateinit var adapter : AdapterGrupoElegido
    private var listaMensajes = mutableListOf<Mensaje>()
    private lateinit var player : ExoPlayer
    private var shortAnimationDuration: Int = 10000000
    var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

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

        adapter = AdapterGrupoElegido(binding,listaMensajes,usuarioActual,this)

        recyclerView.adapter = adapter

        leerMensajesListener(recyclerView)

        player = ExoPlayer.Builder(context!!).build()
        binding.playerViewGrupo?.player = player
        GlobalScope.launch(Dispatchers.IO) {
            //var videoUri = Uri.parse("https://00f74ba44b59be5011d6b18e30943d13514c6957ef-apidata.googleusercontent.com/download/storage/v1/b/tamarindos/o/onupiesitosqa-1013.mp4?jk=AFshE3WGhu5JCzZ-Z9dnz6lurUI10lbyyf7ZjfzvlQNnYhq9kqaZxY40FrNAUmDvhWm4NGNIjUQSyJfTc3LTsFKHihOg71JDa5IV3yDQ6flAEmiJwaAAwvwTCN_-EAqY6I_G2pQM3FBlX_gLzpZAj0GAgS2sR1PZX_ZG8GdJ994SmASVndYRwLH8iOa4QgAVLz2kozq4lvfEScsiS_h_ApZz9MMUUhaCU2Zak3ma9OSFORxZ3c3nAYpoJFuZW-YTWsXQ_6TzyD-HBp1H-e7IpbHmrH0ja7TR193MZ1PZ6CwifUI5gAU--1bQEoouko4I-e3HNtj8EH0xmR1Z0X17ZiOL91HAF_YXsNjUnfTw1DS375rMT0Gsiy6dj62E1uWdROD94SHje25kavDhzFS-64wDcTCFoKntl18D7rTkZ2D1wTKwGR-ghbLDQx33WM0Z_EQZh-f7Yxn9fnKRYTgdOfRCSnI5B4IZeGjvy63wP1zlXdxSPi-SOdvPQBscHVDQcBApnG5RyiMXBb_rVNUiw9z7VDbox2NzeFfwD9_wqayKftusXOIXBccOeAvmm7DBnyUkstKJYagdtqs4oAkyzAQ7PcdFBL6bvpRFgA8wscSLSyaVLOHYAIqd99ThXW7W-Nq-vE1CxapJfsE7AGlEvxTNpsAK3vYEBIpiQmV9jvNmRCq-QWBVVnvdmhv4Y91dTEcNZkUqFcxe-x9jym233THf4G1JBQZdPGq7HufX6wEkHHXXHvWgwxMNmQ_UPYoQnstfCQVuqSxfNJ0Lj3xMwm486DtXAlihwx4imi2XtBIXJ2Seb7Z-0NdV6CTYz1CRmFrg37RmN1gSSUvZ4RXSCdKp1yXCFJXL-DJdbtQzqK0oJgO9ehLr-whYUEuzMpWLJjtyD7SylJMq9xS88rESTasTKFt3E8IcSZEyCs6jN1SDKSNMKc0eod8HBav2EJ5UxXVsYvA9ayM_qWjawtKqobAb6MkSsalfAzwTB48ifem1b94wQ0DY1R66mCPTIW06OZE1UXK1icxM-WYm1_p0pE5_m4tJ4ePm&isca=1")
            //Funciona
            val mediaItem: MediaItem = MediaItem.fromUri(Storage.elegirVideo(usuarioActual))
            //val mediaItem: MediaItem = MediaItem.fromUri(videoUri)

            withContext(Dispatchers.Main) {
                player.setMediaItem(mediaItem)
                player.prepare()
                //player.play()
            }
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        //Para mover el videoview
        //binding.constraintLayoutVideo?.setOnTouchListener(onTouchListener())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.mostrarVideo?.setOnClickListener {
            if(binding.playerViewGrupo?.visibility == View.GONE) {
                ObjectAnimator.ofFloat(binding.playerViewGrupo,"translationY",-550f).apply {
                    this.duration = 0
                    start()
                }.doOnEnd {
                    binding.playerViewGrupo?.visibility  = View.VISIBLE
                    ObjectAnimator.ofFloat(binding.playerViewGrupo,"translationY",90f).apply {
                        this.duration = 500
                        start()
                    }
                }
            }else {
                ObjectAnimator.ofFloat(binding.playerViewGrupo,"translationY",-550f).apply {
                    this.duration = 500
                    start()
                }.doOnEnd {
                    binding.playerViewGrupo?.visibility  = View.GONE
                }
            }

        }
    }

    @SuppressLint("LongLogTag")
    override fun onStart() {
        super.onStart()
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

    private fun hacerVisible(playerViewGrupo: View) {
        playerViewGrupo?.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            this.alpha = 0f
            this.visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }

    private fun hacerNoVisible(playerViewGrupo: View) {
        playerViewGrupo?.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            this.alpha = 1f

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        playerViewGrupo.visibility = View.GONE
                    }
                })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListener(): View.OnTouchListener {
        return View.OnTouchListener { view, event ->
            // position information
            // about the event by the user
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            // detecting user actions on moving
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_BUTTON_PRESS -> {
                    Toast.makeText(
                        context,
                        "Presionas", Toast.LENGTH_SHORT
                    )
                        .show()
                }
                MotionEvent.ACTION_DOWN -> {
                    val lParams = view.layoutParams as RelativeLayout.LayoutParams
                    //xDelta = x - lParams.leftMargin
                    yDelta = y - lParams.topMargin
                }
                /*MotionEvent.ACTION_UP -> Toast.makeText(
                    context,
                    "new location!", Toast.LENGTH_SHORT
                )
                    .show()*/
                MotionEvent.ACTION_MOVE -> {
                    // based on x and y coordinates (when moving image)
                    // and image is placed with it.
                    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
                    //layoutParams.leftMargin = x - xDelta
                    layoutParams.topMargin = y - yDelta
                    layoutParams.rightMargin = 0
                    layoutParams.bottomMargin = 0
                    view.layoutParams = layoutParams
                }
            }
            // reflect the changes on screen
            //R.id.relativeLayoutVideo.invalidate()
            true
        }
    }

}