package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.adapters.AdapterMisGrupos
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MisGruposFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MisGruposFragment(
    var usuarioActual: UsuarioActual,
    var toolbar: androidx.appcompat.widget.Toolbar
) : Fragment() {

    private lateinit var binding : FragmentMisGruposBinding
    private lateinit var listaGrupos : ArrayList<Grupo>
    private lateinit var  adapter : AdapterMisGrupos
    private lateinit var db : FirebaseFirestore
    lateinit var listener : OnFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMisGruposBinding.inflate(layoutInflater)
        var recyclerView = binding.misGruposRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listaGrupos = arrayListOf()
        adapter = AdapterMisGrupos(binding,listaGrupos,usuarioActual,this,toolbar)

        recyclerView.adapter = adapter

        binding.searchViewMisGrupos.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.searchViewMisGrupos.clearFocus()
                if (listaGrupos.contains(Grupo(query))){
                    Log.d("searchView Contiene", "Lo contengo")
                }else {
                    if (query != null) {
                        Log.d("searchView No Contiene",query)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                var filterString = filter(listaGrupos,newText)
                adapter.setFilter(filterString)

                Log.d("searchView Cambia", "Estoy cambiando")
                return true
            }
        })
    }

    private fun filter(strings: ArrayList<Grupo>, text: String): ArrayList<Grupo> {
        var filterString = ArrayList<Grupo>()
        for (word in strings) {
            if (word.nombreGrupo?.contains(text) == true) filterString.add(word)
        }
        return filterString
    }
    override fun onStart() {
        super.onStart()
        toolbar.setTitle("Mis Grupos")
        listaGrupos.clear()
        eventChangeListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var botonInfoGrupo = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonInfoGrupo.setOnClickListener {
            if (context != null) {
                Toast.makeText(context,"Adios", Toast.LENGTH_SHORT).show()
            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Grupos").whereArrayContains("listaParticipantes",usuarioActual.email.toString())
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
                            listaGrupos.add(dc.document.toObject(Grupo::class.java))
                            Log.d("Eventchangelistener documento : ", dc.document.toString())
                        }
                    }
                    Log.d("Eventchangelistener lista : ", listaGrupos.toString())

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

    fun refrescarRecycler() {
        listener.actualizarRecyclerMisGrupos()
    }
}