package com.danielmijens.loginapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.danielmijens.loginapp.R
import com.danielmijens.loginapp.databinding.FragmentVerDatosDeUsuarioBinding
import com.danielmijens.loginapp.entidades.Usuario
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
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
class VerDatosDeUsuarioFragment(
    val usuarioActual: UsuarioActual,
    var toolbar: Toolbar,
    var navigationView: NavigationView
) : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var binding : FragmentVerDatosDeUsuarioBinding
    private lateinit var botonAuxiliar : ImageButton
    private var subidaFoto = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.setTitle("Datos de usuario")
        binding = FragmentVerDatosDeUsuarioBinding.inflate(layoutInflater)
        Log.d("Foto perfil " , usuarioActual.fotoPerfil.toString())
        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        binding.editTextEmail.setText(usuarioActual.email.toString())
        if (usuarioActual.nombreUsuario.isNullOrEmpty()) {
            binding.editTextNombreUsuario.isEnabled = true
            botonAuxiliar.isEnabled = false
            botonAuxiliar.visibility = View.INVISIBLE
            binding.buttonModificar.visibility = View.VISIBLE
            binding.editTextEdad.isEnabled = true
            binding.editTextEdad.setHintTextColor(Color.LTGRAY)
            binding.editTextSerieFavorita.isEnabled = true
            binding.editTextSerieFavorita.setHintTextColor(Color.LTGRAY)
        }else {
            binding.editTextNombreUsuario.setText(usuarioActual.nombreUsuario)
            botonAuxiliar.isEnabled = true
        }

    }

    override fun onStart() {
        super.onStart()
        var prefs = traerPrefs()
        botonAuxiliar.setBackgroundResource(R.mipmap.icono_cambiar_perfil)
       Log.d("UsuarioActual ",usuarioActual.toString())
        if (!usuarioActual.nombreUsuario.isNullOrEmpty() || subidaFoto) {
            Picasso.get().load(usuarioActual.fotoPerfil).into(binding.imageViewFotoPerfil)
        }else {
            Picasso.get().load(R.drawable.add_image).into(binding.imageViewFotoPerfil)
        }
        if (!usuarioActual.nombreUsuario.isNullOrEmpty()) {
            var jsonUsuario = prefs?.getString("usuario","")
            if (!jsonUsuario.isNullOrEmpty()) {
                var usuario : Usuario = Gson().fromJson(jsonUsuario,Usuario::class.java)
                binding.editTextEdad.setText(usuario.edad)
                binding.editTextSerieFavorita.setText(usuario.serieFav)
            }
            botonAuxiliar.isEnabled = true
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

        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAuxiliar.setOnClickListener {
            var botonMod = binding.buttonModificar
            if (botonMod.visibility == View.VISIBLE) {
                botonMod.visibility = View.INVISIBLE
                botonAuxiliar.setBackgroundResource(R.mipmap.icono_cambiar_perfil)
                binding.editTextEdad.isEnabled = false
                binding.editTextEdad.setTextColor(Color.LTGRAY)
                binding.editTextSerieFavorita.isEnabled = false
                binding.editTextSerieFavorita.setTextColor(Color.LTGRAY)
            }else {
                botonMod.visibility = View.VISIBLE
                botonAuxiliar.setBackgroundResource(R.mipmap.icono_cambiar_perfil_cancelar)
                binding.editTextEdad.isEnabled = true
                binding.editTextEdad.setTextColor(Color.WHITE)
                binding.editTextSerieFavorita.isEnabled = true
                binding.editTextSerieFavorita.setTextColor(Color.WHITE)
            }

        }

        binding.buttonModificar.setOnClickListener {
            var nombreUsuarioNuevo = binding.editTextNombreUsuario.text.toString().trim()

            if (nombreUsuarioNuevo.contains(" ")||nombreUsuarioNuevo.isBlank()) {
                showAlert(
                    "El nombre de usuario no puede contener espacios o vacio",
                    "Cambie el nombre de de usuario ",
                )
            }else if (usuarioActual.fotoPerfil.isNullOrEmpty() || usuarioActual.fotoPerfil=="2131230807") {
                showAlert(
                    "La imagen esta por defecto o esta vacia",
                    "Cambie la foto de usuario",
                )
            }else {
                var edadUsuario = binding.editTextEdad.text.toString()
                var serieFav = binding.editTextSerieFavorita.text.toString()
                var nuevoUsuario = Usuario(usuarioActual.email,usuarioActual.fotoPerfil,nombreUsuarioNuevo,edadUsuario,serieFav)
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        Consultas.establecerUsuario(nuevoUsuario)
                        navigationView.getHeaderView(0).findViewById<TextView>(R.id.emailUsuarioNav).setText(nombreUsuarioNuevo)
                        var fotoNav = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.imageViewPerfilUsuario)
                        Picasso.get().load(usuarioActual.fotoPerfil)
                            .into(fotoNav)
                        showAlert("Usuario definido correctamente","Ya puede moverse por la aplicación")
                    }
                    var prefs = traerPrefs()
                    var gson = Gson().toJson(nuevoUsuario)
                    prefs?.edit()?.putString("usuario",gson)?.commit()


                }
                usuarioActual.nombreUsuario = nombreUsuarioNuevo
            }
        }

        binding.imageViewFotoPerfil.setOnClickListener {
            if (!usuarioActual.fotoPerfil?.contains("googleusercontent")!!) {
                if (binding.buttonModificar.visibility == View.VISIBLE) {
                AlertDialog.Builder(context)
                    .setTitle("Va a cambiar la foto de perfil")
                    .setMessage("¿Esta seguro?")
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        openFile(Uri.parse(""))
                    }

                    .setNegativeButton(android.R.string.cancel,  {dialog, which ->

                    })
                    .show()
                }
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
                subidaFoto = true
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        Storage.subirImagenPerfil(usuarioActual,uri)
                    }
                }
            }
        }
    }

    fun traerPrefs(): SharedPreferences?{
        val prefs = activity?.getSharedPreferences(
            getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        return prefs
    }

}