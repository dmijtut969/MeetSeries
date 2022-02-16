package com.danielmijens.loginapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.FragmentBuscarGrupoBinding
import com.google.android.material.snackbar.Snackbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CrearGrupoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BuscarGrupoFragment(var usuarioActual: UsuarioActual) : Fragment() {

    private lateinit var listener : OnFragmentListener
    private lateinit var binding : FragmentBuscarGrupoBinding
    private lateinit var bindingActivity : ActivityUserBinding
    private var userActivity = UserActivity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingActivity = ActivityUserBinding.inflate(layoutInflater)
        binding = FragmentBuscarGrupoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonInfo1.setOnClickListener {
            showAlert("Info Nombre Grupo","Se buscaran los grupos que contengan el nombre del parametro indicado.")
        }

        binding.buttonBusquedaNombreGrupos.setOnClickListener {
            listener.onBuscarClick("nombreGrupo",binding.editTextBusquedaNombreGrupo.text.toString())
        }

        binding.buttonInfo2.setOnClickListener {
            showAlert("Info Categoria","Se buscaran los grupos que contengan la categoria del parametro indicado.")
        }

        binding.buttonBusquedaCategoria.setOnClickListener {
            listener.onBuscarClick("categoriaGrupo",binding.editTextBusquedaCategoria.text.toString())
        }

        binding.buttonInfo3.setOnClickListener {
            showAlert("Info Nombre Grupo","Se buscaran los grupos que contengan el participante del parametro indicado.")
        }

        binding.buttonBusquedaParticipantes.setOnClickListener {
            listener.onBuscarClick("participantes",binding.editTextBusquedaParticipantes.text.toString())
        }
    }

    private fun showAlert(tittle : String,message : String) {
        AlertDialog.Builder(this.context)
            .setTitle(tittle)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->

                })

            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }

}