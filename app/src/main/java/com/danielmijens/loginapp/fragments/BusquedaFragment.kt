package com.danielmijens.loginapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

class BusquedaFragment(var usuarioActual: UsuarioActual, var campo: String, var valorABuscar: String ?= null) : Fragment() {

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


        binding.searchViewMisGrupos.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.searchViewMisGrupos.clearFocus()
                if (listaGruposBusqueda.contains(Grupo(query))){
                    Log.d("searchView Contiene", "Lo contengo")
                }else {
                    if (query != null) {
                        Log.d("searchView No Contiene",query)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                var nuevaBusqueda = arrayListOf<Grupo>()
                nuevaBusqueda.addAll(listaGruposBusqueda)
                var filterString = filter(nuevaBusqueda,newText)
                adapter.setFilter(filterString)

                Log.d("searchView Cambia", "Estoy cambiando")
                return true
            }
        })

    }

    override fun onStart() {
        super.onStart()
        if (!valorABuscar.isNullOrEmpty()) { //Con esto permitimos que si no se busca nada, se traigan todos los grupos existentes.
            eventChangeListener(campo, valorABuscar!!)
        }else {
            eventChangeListenerTodos()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return binding.root
    }

    private fun filter(strings: ArrayList<Grupo>, text: String): ArrayList<Grupo> {
        var filterString = ArrayList<Grupo>()

        for (grupo in strings) {
            if (!filterString.toString().contains(grupo.idGrupo.toString())) {
                if (binding.radioButtonNombre.isChecked) {
                    if (grupo.nombreGrupo?.contains(text) == true) filterString.add(grupo)
                }else if (grupo.listaParticipantes != null && binding.radioButtonParticipantes.isChecked) {
                    for (participante in grupo.listaParticipantes) {
                        if (participante.contains(text)) filterString.add(grupo)
                    }
                }
            }
        }
        return filterString
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
        listaGruposBusqueda.clear()
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
                            Log.d("Eventchangelistener documento : ", dc.document.toString())
                            var grupoEncontrado = dc.document.toObject(Grupo::class.java)
                            if (!grupoEncontrado.listaParticipantes!!.contains(usuarioActual.email.toString())) {
                                listaGruposBusqueda.add(dc.document.toObject(Grupo::class.java))
                            }
                        }
                    }
                    Log.d("Valor a buscar : ", valorABuscar.toString())
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