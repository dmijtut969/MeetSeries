package com.danielmijens.loginapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrupoElegidoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GrupoElegidoFragment(var usuarioActual: UsuarioActual) : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentGrupoElegidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentGrupoElegidoBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}