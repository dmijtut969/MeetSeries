package com.danielmijens.loginapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.UserActivity
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.FragmentCrearGrupoBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
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
 * Use the [CrearGrupoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearGrupoFragment(var usuarioActual: UsuarioActual) : Fragment() {

    private lateinit var binding : FragmentCrearGrupoBinding
    private lateinit var bindingActivity : ActivityUserBinding
    private lateinit var listener : OnFragmentListener
    private lateinit var uriFotoGrupo : Uri
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
        binding.crearFotoGrupoImageView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Va a subir una foto de perfil")
                .setMessage("¿Esta seguro?")
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    openFile(Uri.parse(""))
                }

                .setNegativeButton(android.R.string.cancel,  {dialog, which ->

                })
                .show()
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
            } else if (binding.crearFotoGrupoImageView.drawable.toString().contains("BitmapDrawable")){
                withContext(Dispatchers.Main) {
                    showAlert("No has seleccionado foto de perfil.","Seleccione una.")
                }
            } else {
                Log.d("Fotogrupo drawable: ", binding.crearFotoGrupoImageView.drawable.toString())
                var  idGrupoNuevo = nuevoNombreGrupo + " - " + usuarioActual.email
                subirImagen(idGrupoNuevo)
                Consultas.crearGrupo(usuarioActual,nuevoNombreGrupo,nuevaDescripcionGrupo,"fotoGrupo/" + idGrupoNuevo)
                var creadorGrupo = usuarioActual.email.toString()
                var nuevoGrupoElegido = Grupo(nuevoNombreGrupo,nuevaDescripcionGrupo,arrayListOf<String>(creadorGrupo),creadorGrupo,idGrupoNuevo,Storage.extraerImagenGrupo(idGrupoNuevo).toString())
                listener.onCrearGrupoClick(nuevoGrupoElegido)
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

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        Log.d("onActivityResult  ",  "He entrado con " + requestCode +" y "+ resultCode)
        if (requestCode == 2
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                Log.d("La uri elegida es: ", uri.path.toString())
                Picasso.get().load(uri).into(binding.crearFotoGrupoImageView)
                uriFotoGrupo = uri
            }
        }
    }

    fun subirImagen(idGrupo : String) {
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                Storage.subirImagenGrupo(idGrupo,uriFotoGrupo)
            }

        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }



}