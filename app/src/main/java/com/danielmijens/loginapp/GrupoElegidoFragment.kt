package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.DocumentType

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
    var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentGrupoElegidoBinding.inflate(layoutInflater)
        var recyclerView = binding.mensajesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterGrupoElegido(binding,listaMensajes,usuarioActual,this)

        recyclerView.adapter = adapter

        leerMensajesListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButton.setOnClickListener {
            AlertDialog.Builder(this.context)
                .setTitle("Aqui se podran enviar mensajes entre usuarios del grupo")
                .setMessage("Por implementar...")
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which ->

                    }).show()
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

    fun leerMensajesListener() {
        mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            .collection("Mensajes")//.orderBy("hora",Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null ) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }
                    for (cambioMensaje in value?.documentChanges!!) {
                        if (cambioMensaje.type == DocumentChange.Type.ADDED) {
                            listaMensajes.add(cambioMensaje.document.toObject(Mensaje::class.java))
                            adapter.notifyItemInserted(1)
                        }

                    }
                    Log.d("Se ha cambiado algo", "Ha habido un cambio")

                }

            })
    }

}