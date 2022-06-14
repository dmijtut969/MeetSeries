package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.adapters.AdapterMisGrupos
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.entidades.ControlVideo
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MisGruposFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


public class MisGruposFragment(
    var usuarioActual: UsuarioActual,
    var toolbar: Toolbar,
    var drawer: DrawerLayout ?= null
) : Fragment() {

    private lateinit var binding : FragmentMisGruposBinding
    private lateinit var listaGrupos : ArrayList<Grupo>
    private lateinit var  adapter : AdapterMisGrupos
    private lateinit var db : FirebaseFirestore
    private lateinit var botonAuxiliar : ImageButton
    private lateinit var botonAtras : ImageButton
    lateinit var listener : OnFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = FragmentMisGruposBinding.inflate(layoutInflater)
        var recyclerView = binding.misGruposRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        listaGrupos = arrayListOf()
        adapter = AdapterMisGrupos(binding,listaGrupos,usuarioActual,this,toolbar,drawer)

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
        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAtras = toolbar.rootView.findViewById<ImageButton>(R.id.botonAtras)
    }

    private fun filter(strings: ArrayList<Grupo>, text: String): ArrayList<Grupo> {
        var filterString = ArrayList<Grupo>()
        var buscado = text.uppercase(Locale.getDefault())
        for (word in strings) {
            if (word.nombreGrupo?.uppercase(Locale.getDefault())?.contains(buscado) == true) filterString.add(word)
        }
        return filterString
    }
    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
        toolbar.setTitle("Mis Grupos")
        botonAuxiliar.visibility = View.GONE
        listaGrupos.clear()
        eventChangeListener()
        videoIniciado()
        videoIniciadoGrupos()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var botonInfoGrupo = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        drawer?.findViewById<View>(R.id.nav_logOut)?.visibility = View.VISIBLE
        botonInfoGrupo.setBackgroundResource(R.drawable.icono_meet)
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
                        var grupoNuevo = dc.document.toObject(Grupo::class.java)
                        Log.d("grupoNuevo", grupoNuevo.toString())
                        if (dc.type == DocumentChange.Type.ADDED) {
                            listaGrupos.add(grupoNuevo)
                            Log.d("Eventchangelistener documento : ", dc.document.toString())
                        }
                    }
                    Log.d("Eventchangelistener lista : ", listaGrupos.toString())

                    adapter.notifyDataSetChanged()
                }

            })
    }

    private fun videoIniciado() {
        db = FirebaseFirestore.getInstance()
        GlobalScope.launch (Dispatchers.IO){
            withContext(Dispatchers.Main) {
                var listaControlVideo = db.collection("ControlVideos").get().await().documents
                for (control in listaControlVideo) {
                    var controlObjeto = control.toObject(ControlVideo::class.java)
                    var listaTemporal = arrayListOf<Grupo>()
                    listaTemporal.addAll(listaGrupos)
                    for (grupito in listaGrupos) {
                        if (grupito.idGrupo==controlObjeto?.idGrupo) {
                            listaTemporal.removeAt(listaGrupos.indexOf(grupito))
                            grupito.videoIniciado = controlObjeto?.videoIniciado==true
                            listaTemporal.add(grupito)

                        }
                    }
                    listaGrupos = listaTemporal
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun videoIniciadoGrupos() {
        db = FirebaseFirestore.getInstance()
        db.collection("ControlVideos")
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
                        var controlNuevo = dc.document.toObject(ControlVideo::class.java)
                        Log.d("controlGrupoNuevo", controlNuevo.toString())
                        if (!listaGrupos.isNullOrEmpty() && dc.type == DocumentChange.Type.MODIFIED) {
                            var listaTemporal = arrayListOf<Grupo>()
                            listaTemporal.addAll(listaGrupos)
                            for (grupito in listaGrupos) {
                                if (grupito.idGrupo.equals(controlNuevo.idGrupo) && controlNuevo.videoIniciado == true) {
                                    listaTemporal.remove(grupito)
                                    grupito.videoIniciado = true
                                    listaTemporal.add(grupito)
                                }else if(grupito.idGrupo.equals(controlNuevo.idGrupo) && controlNuevo.videoIniciado == false) {
                                    listaTemporal.remove(grupito)
                                    grupito.videoIniciado = false
                                    listaTemporal.add(grupito)
                                }
                            }
                            listaGrupos = listaTemporal
                            adapter.notifyDataSetChanged()
                        }
                    }
                    Log.d("Eventchangelistener lista : ", listaGrupos.toString())


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
