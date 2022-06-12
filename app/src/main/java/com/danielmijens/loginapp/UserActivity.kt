package com.danielmijens.loginapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.AppBarMainBinding
import com.danielmijens.loginapp.databinding.NavHeaderMainBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.Usuario
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.danielmijens.loginapp.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,OnFragmentListener {
    lateinit var binding : ActivityUserBinding
    lateinit var bindingToolbar: AppBarMainBinding
    lateinit var bindingNavHeader : NavHeaderMainBinding
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var botonAuxiliar : ImageButton
    private lateinit var botonAtras : ImageButton
    lateinit var usuarioActual : UsuarioActual
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        bindingToolbar = AppBarMainBinding.inflate(layoutInflater)
        bindingNavHeader = NavHeaderMainBinding.inflate(layoutInflater)


        usuarioActual = intent.getSerializableExtra("usuario") as UsuarioActual

        setContentView(binding.root)
        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,MisGruposFragment(
            usuarioActual,
            toolbar,
            drawer
        )).commit()
        //Utilidades de navegacion

        toolbar.setTitle("Mis Grupos")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView : NavigationView= findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()

        botonAuxiliar = toolbar.rootView.findViewById<ImageButton>(R.id.botonAuxiliar)
        botonAtras = toolbar.rootView.findViewById<ImageButton>(R.id.botonAtras)
        //botonAuxiliar.visibility = View.GONE
        navigationView.setNavigationItemSelectedListener {
            var permitirMovimiento = 0
            if (!usuarioActual.nombreUsuario.isNullOrEmpty() || it.itemId == R.id.nav_logOut || it.itemId == R.id.nav_verDatosUsuario) {
                permitirMovimiento = it.itemId
            }
            drawer?.findViewById<View>(R.id.nav_logOut)?.visibility = View.VISIBLE
            when (permitirMovimiento) {

                R.id.nav_mis_grupos -> {
                    cambiarFragment(MisGruposFragment(usuarioActual,toolbar,drawer))
                    botonAuxiliar.visibility = View.VISIBLE
                    botonAtras.visibility = View.GONE
                    toolbar.setTitle("Mis Grupos")
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_buscar_grupos -> {
                    cambiarFragment(BusquedaFragment(usuarioActual,"Nombre"))
                    toolbar.setTitle("Busqueda de Grupos")
                    botonAuxiliar.visibility = View.GONE
                    botonAtras.visibility = View.GONE
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_crear_grupo -> {
                    cambiarFragment(CrearGrupoFragment(usuarioActual))
                    toolbar.setTitle("Crear Grupos")
                    botonAuxiliar.visibility = View.GONE
                    botonAtras.visibility = View.GONE
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_verDatosUsuario -> {
                    cambiarFragment(VerDatosDeUsuarioFragment(usuarioActual,toolbar))
                    toolbar.setTitle("Ver Mis Datos")
                    botonAuxiliar.visibility = View.VISIBLE
                    botonAtras.visibility = View.GONE
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_logOut -> {
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Va a cerrar sesion")
                        .setMessage("¿Esta seguro?")
                        .setPositiveButton(android.R.string.ok,
                            DialogInterface.OnClickListener { dialog, which ->
                                try {
                                    logOut()
                                    finish()
                                }catch (e : FirebaseFirestoreException) {
                                    Log.d("Fallo permisos", " Fallo Permisos")
                                }

                                //super.onBackPressed()
                            })
                        .setNegativeButton(android.R.string.cancel,
                            DialogInterface.OnClickListener { dialog, which ->
                            })
                        .show()
                    false
                }
                0 -> {
                    drawer.closeDrawer(GravityCompat.START)
                    makeAlert("No tiene permisos para moverse por la app","Debe ingresar un nombre de usuario y una foto de perfil")
                    false
                }
                else -> false
            }
        }
        //Termino utilidades de navegacion
        bindingNavHeader.emailUsuarioNav.text = usuarioActual.email.toString()


        GlobalScope.launch(Dispatchers.IO) {
            val navigationView : NavigationView= findViewById(R.id.nav_view)
            var foto = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.imageViewPerfilUsuario)
            usuarioActual.nombreUsuario = Consultas.sacarNombreUsuario(Usuario(usuarioActual.email)).toString()
            var nombreUsu = navigationView.getHeaderView(0).findViewById<TextView>(R.id.emailUsuarioNav)
            nombreUsu.setText(usuarioActual.nombreUsuario.toString())

            if (FirebaseAuth.getInstance().currentUser?.photoUrl == null) {
                var nuevaFoto = Storage.extraerImagenPerfil(usuarioActual?.email.toString()).toString()
                if (!nuevaFoto.isNullOrEmpty()) {
                    usuarioActual.fotoPerfil = nuevaFoto
                }else {
                    usuarioActual.fotoPerfil = R.drawable.auth_logo_xml.toString()
                }
                withContext(Dispatchers.Main) {
                    Picasso.get().load(usuarioActual.fotoPerfil).into(foto)
                }
            } else {
                withContext(Dispatchers.Main) {
                    var fotoUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
                    Picasso.get().load(fotoUrl)
                        .into(foto)
                    usuarioActual.fotoPerfil = fotoUrl
                }
            }
            if (!Consultas.comprobarNombreUsuario(usuarioActual)){
                cambiarFragment(VerDatosDeUsuarioFragment(usuarioActual,toolbar))
            }
        }

        //Guardado de datos

        val prefs = traerPrefs().edit()
        prefs?.putString("emailUsuarioActual", usuarioActual.email)
        prefs?.apply()
    }

    fun traerPrefs(): SharedPreferences{
        val prefs = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        )
        return prefs
    }

    private fun logOut() {
        val prefs = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        ).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    fun cambiarFragment(fragmentNuevo : Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragmentContainerView,fragmentNuevo).addToBackStack(fragmentNuevo.tag).commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()

    }

    override fun onBackPressed() {
        if (!usuarioActual.nombreUsuario.isNullOrEmpty()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.show(WindowInsets.Type.statusBars())
            }
            if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
            } else {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Va a cerrar sesion")
                    .setMessage("¿Esta seguro?")
                    .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            logOut()
                            super.onBackPressed()
                        })
                    .setNegativeButton(android.R.string.cancel,
                        DialogInterface.OnClickListener { dialog, which ->
                        })
                    .show()
            }
        }   else {
            makeAlert("No tiene permisos para moverse por la app","Debe ingresar un nombre de usuario y una foto de perfil")
        }
    }
    override fun onStart() {
        super.onStart()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCrearGrupoClick(grupoElegido: Grupo) {
        cambiarFragment(GrupoElegidoFragment(usuarioActual, grupoElegido,toolbar))
    }

    override fun onElegirGrupoClick(
        usuarioActual: UsuarioActual, grupoElegido: Grupo,
        toolbar: androidx.appcompat.widget.Toolbar
    ) {
        cambiarFragment(GrupoElegidoFragment(usuarioActual, grupoElegido,toolbar))
    }

    override fun onElegirCategoria() {
        cambiarFragment(MisGruposFragment(usuarioActual, toolbar, drawer))
    }

    override fun onVerMisGruposClick() {
        cambiarFragment(MisGruposFragment(usuarioActual, toolbar, drawer))
    }

    override fun onBuscarClick(campo: String, valorABuscar: String) {
        cambiarFragment(BusquedaFragment(usuarioActual,campo,valorABuscar))
    }

    override fun actualizarRecyclerMisGrupos() {
        cambiarFragment(MisGruposFragment(usuarioActual, toolbar, drawer))
    }

    override fun onVerInfoGrupo(grupoElegido: Grupo, toolbar: Toolbar) {
        cambiarFragment(InfoGrupoFragment(grupoElegido,toolbar))
    }

    fun showDialogAlert(usuarioActual: UsuarioActual) {
        AlertDialog.Builder(application)
            .setTitle("Tiene que elegir un nombre de usuario : ")
            .setMessage("¿Esta seguro?")
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .show()
    }

    fun makeAlert(titulo : String, mensaje : String) {
        AlertDialog.Builder(binding.root.context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                })
            .show()
    }

    fun cerrarSesionAtrasAlert(titulo : String, mensaje : String) {

    }
    suspend fun actualizarNavView(usuarioActual: UsuarioActual) {
        val navigationView : NavigationView= findViewById(R.id.nav_view)
        var nombreUsu = navigationView.getHeaderView(0).findViewById<TextView>(R.id.emailUsuarioNav)
        nombreUsu.setText(usuarioActual.nombreUsuario.toString())
    }
}