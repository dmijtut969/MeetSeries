package com.danielmijens.loginapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.adapters.AdapterBusqueda
import com.danielmijens.loginapp.databinding.FragmentBusquedaBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BusquedaFragment(var usuarioActual: UsuarioActual, var campo: String, var valorABuscar: String) : Fragment() {

    private lateinit var binding : FragmentBusquedaBinding
    private lateinit var listaGruposBusqueda : ArrayList<Grupo>
    private lateinit var  adapter : AdapterBusqueda
    private lateinit var db : FirebaseFirestore
    lateinit var listener : OnFragmentListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBusquedaBinding.inflate(layoutInflater)
        var recyclerView = binding.busquedaRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listaGruposBusqueda = arrayListOf()
        adapter = AdapterBusqueda(binding,listaGruposBusqueda,usuarioActual,this)

        recyclerView.adapter = adapter
        if (!valorABuscar.isNullOrEmpty()) { //Con esto permitimos que si no se busca nada, se traigan todos los grupos existentes.
            eventChangeListener(campo,valorABuscar)
        }else {
            eventChangeListenerTodos()
        }

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return binding.root
    }

    private fun eventChangeListener(campo : String,valorABuscar: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").whereEqualTo(campo,valorABuscar)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("LongLogTag")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error",error.message.toString())
                        return
                    }
                    Log.d("Value del document ", value!!.documents.toString())
                    for (dc : DocumentChange in value?.documentChanges!!) {
                        Log.d("Contadorrrr", dc.document.toString())
                        if (dc.type == DocumentChange.Type.ADDED) {
                            listaGruposBusqueda.add(dc.document.toObject(Grupo::class.java))
                            Log.d("Eventchangelistener documento : ", dc.document.toString())
                        }
                    }
                    Log.d("Valor a buscar : ", valorABuscar?.toString())
                    Log.d("Eventchangelistener lista : ", listaGruposBusqueda.toString())

                    adapter.notifyDataSetChanged()
                }

            })
    }
    private fun eventChangeListenerTodos() {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("LongLogTag")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error",error.message.toString())
                        return
                    }
                    Log.d("Value del document ", value!!.documents.toString())
                    for (dc : DocumentChange in value?.documentChanges!!) {
                        Log.d("Contadorrrr", dc.document.toString())
                        if (dc.type == DocumentChange.Type.ADDED) {
                            listaGruposBusqueda.add(dc.document.toObject(Grupo::class.java))
                            Log.d("Eventchangelistener documento : ", dc.document.toString())
                        }
                    }
                    Log.d("Valor a buscar : ", valorABuscar?.toString())
                    Log.d("Eventchangelistener lista : ", listaGruposBusqueda.toString())

                    adapter.notifyDataSetChanged()
                }

            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }

}