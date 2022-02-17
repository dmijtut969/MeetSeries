package com.danielmijens.loginapp

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.AppBarMainBinding
import com.danielmijens.loginapp.databinding.NavHeaderMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,OnFragmentListener {
    lateinit var binding : ActivityUserBinding
    lateinit var bindingToolbar: AppBarMainBinding
    lateinit var bindingNavHeader : NavHeaderMainBinding
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    lateinit var usuarioActual : UsuarioActual
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserBinding.inflate(layoutInflater)
        bindingToolbar = AppBarMainBinding.inflate(layoutInflater)
        bindingNavHeader = NavHeaderMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        usuarioActual = intent.getSerializableExtra("usuario") as UsuarioActual
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,MisGruposFragment(usuarioActual)).commit()

        setContentView(binding.root)

        //Utilidades de navegacion

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView : NavigationView= findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_mis_grupos -> {
                    cambiarFragment(MisGruposFragment(usuarioActual))
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_buscar_grupos -> {
                    Snackbar.make(binding.root, "Aqui se podran buscar grupos", Snackbar.LENGTH_SHORT).show()
                    cambiarFragment(BuscarGrupoFragment(usuarioActual))
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_crear_grupo -> {
                    cambiarFragment(CrearGrupoFragment(usuarioActual))
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_verDatosUsuario -> {
                    Snackbar.make(binding.root, "Aqui se mostraran los datos de usuario", Snackbar.LENGTH_SHORT).show()
                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_cambiarFotoPerfil -> {

                    drawer.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_logOut -> {
                    logOut()
                    false
                }
                else -> false
            }

        }
        //Termino utilidades de navegacion


        bindingNavHeader.emailUsuarioNav.text = usuarioActual.email.toString()
        var email = navigationView.getHeaderView(0).findViewById<TextView>(R.id.emailUsuarioNav)
        email.setText(usuarioActual.email.toString())

        var foto = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.imageViewPerfilUsuario)
        Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl.toString()).into(foto)


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
        onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    fun cambiarFragment(fragmentNuevo : Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragmentContainerView,fragmentNuevo).commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
        //bindingNavHeader.emailUsuarioNav.text = "patata"

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

    override fun onCrearGrupoClick(nuevoNombreGrupo : String,nuevaDescripcionGrupo : String) {
        cambiarFragment(ElegirCategoriaFragment(usuarioActual,nuevoNombreGrupo,nuevaDescripcionGrupo))
    }

    override fun onElegirGrupoClick(usuarioActual: UsuarioActual) {
        cambiarFragment(GrupoElegidoFragment(usuarioActual))
    }

    override fun onElegirCategoria() {
        cambiarFragment(MisGruposFragment(usuarioActual))
    }

    override fun onBuscarClick(campo: String, valorABuscar: String) {
        cambiarFragment(BusquedaFragment(usuarioActual,campo,valorABuscar))
    }

    override fun actualizarRecyclerMisGrupos() {
        cambiarFragment(MisGruposFragment(usuarioActual))
    }


}