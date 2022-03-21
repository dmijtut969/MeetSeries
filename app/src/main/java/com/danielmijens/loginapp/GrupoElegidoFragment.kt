package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
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
    var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

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
        var videoUri = Uri.parse("https://s-delivery33.mxdcontent.net/v/b7238e6b97aee4bfd3c05bf68579ad6f.mp4?s=pPcePFG6y7CO2lnH0k3Nog&e=1647897243&_t=1647882576")
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        //player.play()
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
                binding.playerViewGrupo?.visibility = View.VISIBLE
            }else {
                binding.playerViewGrupo?.visibility = View.GONE
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

}