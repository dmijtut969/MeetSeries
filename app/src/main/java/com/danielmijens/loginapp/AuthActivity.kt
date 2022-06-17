package com.danielmijens.loginapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.danielmijens.loginapp.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.danielmijens.loginapp.entidades.UsuarioActual


class AuthActivity : AppCompatActivity() {

    lateinit var binding : ActivityAuthBinding
    lateinit var mAuth : FirebaseAuth
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.AppTheme)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.imageView2.setImageResource(R.drawable.splash)
        binding.googleButton.setImageResource(R.drawable.logo_google)
        val videoView = binding.videoViewBackground
        videoView.setVideoURI(
            Uri.parse("android.resource://"
                + packageName + "/" + R.raw.background_animado))
        videoView.setOnPreparedListener { it.isLooping = true}
        mAuth = FirebaseAuth.getInstance()

        buttonListeners()
        session()
    }

    //Listener de los botones y campos
    private fun buttonListeners() {
        val editTextEmail = binding.editTextEmail
        val editTextPass= binding.editTextTextPassword
        binding.btnRegistrarse.setOnClickListener {
            if (!editTextEmail.text.isNullOrEmpty() && !editTextPass.text.isNullOrEmpty()) {
                if (editTextPass.text.length >= 6) {
                    registrarUsuario()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "El password tiene que ser de mas de 6 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Algun campo esta sin rellenar", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.buttonIniciarSesion.setOnClickListener {
            if (!editTextEmail.text.isNullOrEmpty() && !editTextPass.text.isNullOrEmpty()) {
                if (editTextPass.text.length >= 6) {
                    signInUsuario()
                } else {
                    Toast.makeText(applicationContext,
                        "El password tiene que ser de mas de 6 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Algun campo esta sin rellenar", Toast.LENGTH_SHORT)
                    .show()
            }
        }

       binding.googleButton.setOnClickListener {
           val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id_string)).requestEmail().build()

           val googleClient = GoogleSignIn.getClient(this,googleConf)
           googleClient.signOut()
           startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
       }

        binding.animationView.setOnClickListener {
            if (binding.animationView.isAnimating ){
                binding.animationView.pauseAnimation()
            }else {
                binding.animationView.playAnimation()
            }
        }

    }

    //Funcion que detecta el inicio de sesión de Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account !=null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            irAUserActivity(account.email ?: "")
                        }else {
                            Toast.makeText(applicationContext, "No se ha podido iniciar sesion", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (e : ApiException) {
                Toast.makeText(applicationContext, "No se ha podido iniciar sesion, ha salido una excepcion", Toast.LENGTH_SHORT).show()
            }


        }


    }

    override fun onStart() {
        super.onStart()
        binding.constraintLayoutAuth?.visibility = View.VISIBLE
        binding.videoViewBackground.start()

    }

    //Si la sesion estaba iniciada, saltara al UserActivity
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE)
        val email = prefs.getString("emailUsuarioActual",null)
        if (email!=null) {
            binding.constraintLayoutAuth?.visibility = View.INVISIBLE
            irAUserActivity(email)
        }
    }

    //Funcion para registrar el usuario en Firebase Authentication
    private fun registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),
            binding.editTextTextPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show()
                signInUsuario()
            }else{
                Toast.makeText(applicationContext, "No se ha podido registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Funcion para iniciar sesion del usuario en Firebase Authentication
    private fun signInUsuario() {
        mAuth.signInWithEmailAndPassword(binding.editTextEmail.text.toString(),
            binding.editTextTextPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                irAUserActivity(binding.editTextEmail.text.toString())
            }else {
                Toast.makeText(applicationContext, "No se ha podido iniciar sesion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Funcion para ir al UserActivity
    private fun irAUserActivity(email : String) {
        val usuarioActual = UsuarioActual(email, "","")
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("usuario", usuarioActual)
        startActivity(intent)
        Toast.makeText(applicationContext, "Se ha iniciado sesion", Toast.LENGTH_SHORT).show()
    }
}