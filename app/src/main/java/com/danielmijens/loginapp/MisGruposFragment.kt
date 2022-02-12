package com.danielmijens.loginapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding

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
    private var listaGrupos : ArrayList<Grupo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMisGruposBinding.inflate(layoutInflater)
        binding.misGruposRecyclerView.layoutManager = LinearLayoutManager(context)

        var adapter : AdapterMisGrupos = AdapterMisGrupos(binding,listaGrupos)

        binding.misGruposRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crearListaGrupos()
    }

    fun crearListaGrupos() {
        Consultas.consultaGruposCreadosPorUsuario(usuarioActual)
        Log.d(TAG,"Lista en fragment : ${Consultas.listaGruposEncontrados}")
        listaGrupos.addAll(Consultas.listaGruposEncontrados)
    }
}