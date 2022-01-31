package com.danielmijens.loginapp

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.danielmijens.loginapp.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityUserBinding
    lateinit var bindingToolbar: AppBarMainBinding
    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserBinding.inflate(layoutInflater)
        bindingToolbar = AppBarMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

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
                R.id.verDatosUsuario -> {
                    Snackbar.make(binding.root, "Aqui se mostraran los datos de usuario", Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.cambiarFotoPerfil -> {
                    Snackbar.make(binding.root, "Aqui se cambiara la foto de perfil del usuario", Snackbar.LENGTH_SHORT).show()
                    true                }
                R.id.logOutNav -> {
                    logOut()
                    true
                }
                else -> false
            }

        }
        //Termino utilidades de navegacion

        var usuarioActual = intent.getSerializableExtra("usuario") as UsuarioActual
        binding.textViewEmailUsuario.text = usuarioActual.email.toString()

        binding.buttonLogOut.setOnClickListener {
            logOut()
        }

        //Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE).edit()
        prefs.putString("emailUsuarioActual",usuarioActual.email)
        prefs.apply()
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
        Snackbar.make(binding.root, "Se ha pulsado un item", Snackbar.LENGTH_SHORT).show()
        when(item.itemId) {
            R.id.verDatosUsuario -> Snackbar.make(binding.root, "Se ha pulsado prueba1", Snackbar.LENGTH_SHORT).show()

            R.id.cambiarFotoPerfil -> Toast.makeText(this,"Hola",Toast.LENGTH_SHORT).show()

            R.id.logOutNav -> logOut()

         }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()

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

}