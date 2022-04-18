package com.danielmijens.loginapp

import android.app.Activity
import android.app.AlertDialog
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
import com.danielmijens.loginapp.databinding.FragmentVerDatosDeUsuarioBinding
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
 * Use the [VerDatosDeUsuarioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerDatosDeUsuarioFragment(val usuarioActual: UsuarioActual) : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding : FragmentVerDatosDeUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVerDatosDeUsuarioBinding.inflate(layoutInflater)
        Log.d("Foto perfil " , usuarioActual.fotoPerfil.toString())

        binding.editTextEmail.setText(usuarioActual.email.toString())
        if (usuarioActual.nombreUsuario.isNullOrEmpty()) {
            binding.editTextNombreUsuario.isEnabled = true
        }else {
            binding.editTextNombreUsuario.setText(usuarioActual.nombreUsuario)
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch (Dispatchers.IO){
            withContext(Dispatchers.Main) {
                Log.d("UsuarioActual ",usuarioActual.toString())
                if (!usuarioActual.fotoPerfil.isNullOrEmpty()) {
                    Picasso.get().load(usuarioActual.fotoPerfil).into(binding.imageViewFotoPerfil)
                }else {
                    Picasso.get().load(R.drawable.icono_meet).into(binding.imageViewFotoPerfil)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonModificar.setOnClickListener {
            var nombreUsuarioNuevo = binding.editTextNombreUsuario.text.toString().trim()

            if (nombreUsuarioNuevo.contains(" ")||nombreUsuarioNuevo.isBlank()) {
                showAlert(
                    "El nombre de usuario no puede contener espacios o estar en blanco",
                    "Cambie el nombre de de usuario ",
                )
            }else {
                usuarioActual.nombreUsuario = nombreUsuarioNuevo
                GlobalScope.launch(Dispatchers.IO) {
                    Consultas.establecerNombreUsuario(usuarioActual)
                    withContext(Dispatchers.Main) {
                        binding.editTextNombreUsuario.isEnabled = false
                    }
                    var intent =  Intent(context, UserActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    //UserActivity().actualizarNavView(usuarioActual)
                }
            }
        }

        binding.imageViewFotoPerfil.setOnLongClickListener {
            if (!usuarioActual.fotoPerfil?.contains("googleusercontent")!!) {
                AlertDialog.Builder(context)
                    .setTitle("Va a cambiar la foto de perfil")
                    .setMessage("¿Esta seguro?")
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        openFile(Uri.parse(""))
                    }

                    .setNegativeButton(android.R.string.cancel,  {dialog, which ->

                    })
                    .show()
            }else {
                AlertDialog.Builder(context)
                    .setTitle("No puedes cambiar la foto de perfil")
                    .setMessage("Tu foto de perfil es la de tu cuenta de Google")
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                    }.show()
            }

            true
        }
    }


    fun showAlert(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                })
            .show()
    }



    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        Log.d("onActivityResult  ",  "He entrado con " + requestCode +" y "+ resultCode)
        if (requestCode == 1
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                Log.d("La uri elegida es: ", uri.path.toString())

                Picasso.get().load(uri).into(binding.imageViewFotoPerfil)
                usuarioActual.fotoPerfil = uri.toString()
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        Storage.subirImagenPerfil(usuarioActual,uri)
                    }

                }
            }
        }
    }

}