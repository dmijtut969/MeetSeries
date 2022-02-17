package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.*
import kotlin.collections.ArrayList

class Consultas() {

    companion object {
        var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

        fun  crearGrupo(
            usuarioActual: UsuarioActual,
            nombreGrupo: String,
            descripcionGrupo: String,
            categoriaGrupo: String
        ) : Boolean{
            var mapaGrupo : Map<String,String> = mapOf("creador" to usuarioActual.email.toString(),
                "nombreGrupo" to nombreGrupo,
                "categoriaGrupo" to categoriaGrupo,
                "descripcionGrupo" to descripcionGrupo,
                "participantes" to usuarioActual.email.toString())
            var todoCorrecto = true
            mFirestore.collection("Grupos").add(mapaGrupo).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }
            return todoCorrecto
        }

        fun borrarGrupo (usuarioActual: UsuarioActual,
                         nombreGrupo: String) {
            mFirestore.collection("Grupos").whereEqualTo("creador",usuarioActual.email).whereEqualTo("nombreGrupo",nombreGrupo)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    @SuppressLint("LongLogTag")
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            Log.e("Firestore Error",error.message.toString())
                            return
                        }
                        for (dc : DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                mFirestore.collection("Grupos").document(dc.document.id).delete()

                                Log.d("Se ha borrado", dc.document.id.toString())
                            }
                        }

                    }

                })


        }


    }


}