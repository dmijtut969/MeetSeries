package com.danielmijens.loginapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.R
import com.danielmijens.loginapp.adapters.AdapterInfoGrupo
import com.danielmijens.loginapp.databinding.FragmentInfoGrupoBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.Usuario
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
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
 * Use the [InfoGrupoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoGrupoFragment(var grupoElegido: Grupo, var toolbar: Toolbar) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentInfoGrupoBinding
    private var listaUsuarios = mutableListOf<Usuario>()
    private lateinit var  adapter : AdapterInfoGrupo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentInfoGrupoBinding.inflate(layoutInflater)
        var recyclerView = binding.recyclerViewInfoParticipantes

        var linearLayout = LinearLayoutManager(context)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        linearLayout.stackFromEnd = false
        linearLayout.reverseLayout = false
        recyclerView.layoutManager = linearLayout
        //recyclerView.setHasFixedSize(true)

        //listaUsuarios = arrayListOf<Usuario>()
        //listaUsuarios.add(Usuario("paquito","dadad","dani"))
        adapter = AdapterInfoGrupo(binding, listaUsuarios,this,toolbar)

        recyclerView.adapter = adapter
        leerMensajesListener(recyclerView)
        GlobalScope.launch(Dispatchers.IO) {
            var uriFotoElegido = Storage.extraerImagenGrupo(grupoElegido.idGrupo)
            withContext(Dispatchers.Main) {
                Picasso.get().load(uriFotoElegido).into(binding.infoImagenGrupo)
            }
        }
        binding.textViewInfoNombreGrupo.setText(grupoElegido.nombreGrupo)

        toolbar.setTitle("")
        var botonAuxiliar = toolbar.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAuxiliar.visibility = View.GONE

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
        for (emailUsuario in grupoElegido.listaParticipantes!!) {
            GlobalScope.launch(Dispatchers.IO) {
                var usuarioAdd = Consultas.sacarUsuario(emailUsuario)
                if (usuarioAdd != null&&!listaUsuarios.contains(usuarioAdd)) {
                    listaUsuarios.add(usuarioAdd)
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }



    }
    override fun onStart() {
        super.onStart()
        listaUsuarios.clear()
    }



}