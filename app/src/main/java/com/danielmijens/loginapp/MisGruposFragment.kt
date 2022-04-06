package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
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
class MisGruposFragment(var usuarioActual: UsuarioActual) : Fragment() {

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
        adapter = AdapterMisGrupos(binding,listaGrupos,usuarioActual,this)

        recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        listaGrupos.clear()
        eventChangeListener()
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