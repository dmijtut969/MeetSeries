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
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.R
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

class CrearGrupoFragment(var usuarioActual: UsuarioActual, var toolbar: Toolbar) : Fragment() {

    private lateinit var binding : FragmentCrearGrupoBinding
    private lateinit var bindingActivity : ActivityUserBinding
    private lateinit var listener : OnFragmentListener
    private lateinit var uriFotoGrupo : Uri
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
        toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar).visibility = View.GONE
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

    //Funcion para hacer comprobaciones previas al crear el grupo.
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
                var creador = usuarioActual.email.toString()
                var idGrupo = nuevoNombreGrupo + " - " + creador
                Consultas.crearControlVideo(idGrupo,creador)
                Consultas.crearGrupo(usuarioActual,nuevoNombreGrupo,nuevaDescripcionGrupo,"fotoGrupo/" + idGrupoNuevo)
                var creadorGrupo = usuarioActual.email.toString()
                var nuevoGrupoElegido = Grupo(nuevoNombreGrupo,nuevaDescripcionGrupo,arrayListOf<String>(creadorGrupo),creadorGrupo,idGrupoNuevo,Storage.extraerImagenGrupo(idGrupoNuevo).toString())
                listener.onCrearGrupoClick(nuevoGrupoElegido)
            }
        }
    }

    //Funcion para hacer alertas.
    fun showAlert(titulo : String, mensaje : String) {
        AlertDialog.Builder(this.context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->

                }).show()
    }

    //Funcion que empieza el intent para pickear una foto de grupo.
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

    //Recoje la foto elegida para su procesado.
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

    //Crea una corutina para subir la imagen elegida a Firestore Storage.
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