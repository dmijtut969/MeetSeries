package com.danielmijens.loginapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.UserActivity
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.FragmentCrearGrupoBinding
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
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
 * Use the [CrearGrupoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearGrupoFragment(var usuarioActual: UsuarioActual) : Fragment() {

    private lateinit var binding : FragmentCrearGrupoBinding
    private lateinit var bindingActivity : ActivityUserBinding
    private lateinit var listener : OnFragmentListener
    private var userActivity = UserActivity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingActivity = ActivityUserBinding.inflate(layoutInflater)
        binding = FragmentCrearGrupoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCrearGrupo.setOnClickListener {
            comprobarGrupo()
        }
    }

    private fun comprobarGrupo() {
        var nuevoNombreGrupo = binding.editTextNombreGrupo.text.toString()
        var nuevaDescripcionGrupo = binding.editTextDescripcionGrupo.text.toString()
        GlobalScope.launch(Dispatchers.IO) {
            if (nuevoNombreGrupo.isNullOrEmpty()) { //La descripcion si puede estar vacia.
                withContext(Dispatchers.Main) {
                showAlert("No puede crear el grupo.", "El campo nombre de grupo esta vacio.")
                }
            } else if (Consultas.existeGrupoPorID(nuevoNombreGrupo + " - " + usuarioActual.email.toString())) {
                withContext(Dispatchers.Main) {
                    showAlert("Ya has creado un grupo con ese nombre.", "Cambie el nombre del grupo.")
                }
            } else {
                listener.onCrearGrupoClick(nuevoNombreGrupo, nuevaDescripcionGrupo)
            }
        }
    }

    fun showAlert(titulo : String, mensaje : String) {
        AlertDialog.Builder(this.context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->

                }).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }



}