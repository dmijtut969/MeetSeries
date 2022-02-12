package com.danielmijens.loginapp

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Consultas() {

    companion object {
        var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        fun  crearGrupo(usuarioActual: UsuarioActual,nombreGrupo : String,descripcionGrupo : String) {
            var mapaGrupo : Map<String,String> = mapOf("creador" to usuarioActual.email.toString(),
                "nombreGrupo" to nombreGrupo,
                "descripcionGrupo" to descripcionGrupo,
                "participantes" to usuarioActual.email.toString())
            mFirestore.collection("grupos").document().set(mapaGrupo)
        }

    }


}