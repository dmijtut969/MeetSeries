package com.danielmijens.loginapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.danielmijens.loginapp.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class AuthActivity : AppCompatActivity() {

    lateinit var binding : ActivityAuthBinding
    lateinit var mAuth : FirebaseAuth
    private val GOOGLE_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)

        setTheme(R.style.AppTheme)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imageView2.setImageResource(R.drawable.auth_logo)
        mAuth = FirebaseAuth.getInstance()


        buttonListeners()
        session()


    }

    private fun buttonListeners() {

        val editTextEmail = binding.editTextEmail
        val editTextPass= binding.editTextTextPassword

        binding.btnRegistrarse.setOnClickListener {
            if (!editTextEmail.text.isNullOrEmpty() && !editTextPass.text.isNullOrEmpty()) {
                if (editTextPass.text.length >= 6) {
                    registrarUsuario()
                } else {
                    Snackbar.make(
                        binding.root,
                        "El password tiene que ser de mas de 6 caracteres",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(binding.root, "Algun campo esta sin rellenar", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.buttonIniciarSesion.setOnClickListener {
            if (!editTextEmail.text.isNullOrEmpty() && !editTextPass.text.isNullOrEmpty()) {
                if (editTextPass.text.length >= 6) {
                    signInUsuario()
                } else {
                    Snackbar.make(
                        binding.root,
                        "El password tiene que ser de mas de 6 caracteres",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(binding.root, "Algun campo esta sin rellenar", Snackbar.LENGTH_SHORT)
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

    }

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
                            Snackbar.make(binding.root, "No se ha podido iniciar sesion", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch (e : ApiException) {
                Snackbar.make(binding.root, "No se ha podido iniciar sesion, ha salido una excepcion", Snackbar.LENGTH_SHORT).show()
            }


        }


    }
    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE

    }
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE)
        val email = prefs.getString("emailUsuarioActual",null)
        if (email!=null) {
            binding.authLayout.visibility = View.INVISIBLE
            irAUserActivity(email)
        }
    }

    private fun registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),
            binding.editTextTextPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(binding.root, "Se ha registrado el usuario", Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(binding.root, "No se ha podido registrar el usuario", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInUsuario() {
        mAuth.signInWithEmailAndPassword(binding.editTextEmail.text.toString(),
            binding.editTextTextPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                irAUserActivity(binding.editTextEmail.text.toString())
            }else {
                Snackbar.make(binding.root, "No se ha podido iniciar sesion", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAUserActivity(email : String) {
        val usuarioActual = UsuarioActual(email, 0)
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("usuario", usuarioActual)
        startActivity(intent)
        Snackbar.make(binding.root, "Se ha iniciado sesion", Snackbar.LENGTH_SHORT).show()
    }
}