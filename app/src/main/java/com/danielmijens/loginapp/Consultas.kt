package com.danielmijens.loginapp

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.danielmijens.loginapp.databinding.ActivityUserBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class Consultas() {

    companion object {
        var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        var listaGruposEncontrados = ArrayList<Grupo>()
        fun  crearGrupo(usuarioActual: UsuarioActual,nombreGrupo : String,descripcionGrupo : String) : Boolean{
            var mapaGrupo : Map<String,String> = mapOf("creador" to usuarioActual.email.toString(),
                "nombreGrupo" to nombreGrupo,
                "descripcionGrupo" to descripcionGrupo,
                "participantes" to usuarioActual.email.toString())
            var todoCorrecto = true
            mFirestore.collection("grupos").add(mapaGrupo).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }
            return todoCorrecto
        }

        fun consultaGruposCreadosPorUsuario (usuarioActual: UsuarioActual) {
            val gruposCreadores : Query = mFirestore.collection("grupos").whereEqualTo("creador",usuarioActual.email)
            listaGruposEncontrados.removeAll(listaGruposEncontrados)
            var lista = ArrayList<Grupo>()
            gruposCreadores.get().addOnSuccessListener { grupos ->
                for (grupo in grupos) {
                    var grupoEncontrado = grupo.data.get("nombreGrupo")
                    Log.d(TAG,"Nombre: ${grupoEncontrado.toString()}")
                    lista.add(Grupo(grupoEncontrado.toString(),0))
                }
                Log.d(TAG, "Lista de grupos encontrados : $lista")
                sacarListaEncontrada(lista)

            }

        }

        fun sacarListaEncontrada (listaEncontrada :  ArrayList<Grupo>) : ArrayList<Grupo> {
            listaGruposEncontrados.removeAll(listaGruposEncontrados)
            listaGruposEncontrados.addAll(listaEncontrada)
            Log.d(TAG,"Lista en funciona sacarListaEncontrada: ${listaGruposEncontrados}")
            return listaGruposEncontrados
        }

    }


}