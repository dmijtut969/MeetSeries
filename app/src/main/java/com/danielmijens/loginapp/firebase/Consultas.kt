package com.danielmijens.loginapp.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.Mensaje
import com.danielmijens.loginapp.entidades.Usuario
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Consultas() {

    companion object {
        var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()


        suspend fun  crearGrupo(
            usuarioActual: UsuarioActual,
            nombreGrupo: String,
            descripcionGrupo: String,
            categoriaGrupo: String,
            fotoGrupo : String
        ) : Boolean{
            var creador = usuarioActual.email.toString()
            var listaParticipantes = arrayListOf<String>(creador)
            var idGrupo = nombreGrupo + " - " + creador
            var nuevoGrupo = Grupo(nombreGrupo,categoriaGrupo,descripcionGrupo,listaParticipantes,creador,idGrupo,fotoGrupo)
            var todoCorrecto = true
            var grupoNuevoRef  = mFirestore.collection("Grupos").document(idGrupo)
            grupoNuevoRef.set(nuevoGrupo).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }.await()
            /*grupoNuevoRef.collection("Mensajes").add(Mensaje("Aqui va el emisor", "Bienvenido! Este es un mensaje de prueba",hora)).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }*/

            return todoCorrecto
        }

        fun borrarGrupo (usuarioActual: UsuarioActual,
                         nombreGrupo: String) {
            var idGrupo = nombreGrupo + " - " + usuarioActual.email.toString()
            mFirestore.collection("Grupos").whereEqualTo("idGrupo",idGrupo)
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

        fun buscarGrupoPorID (idGrupo : String) : Grupo {
            var grupoBuscado : Grupo = Grupo()
            val docRef = mFirestore.collection("Grupos").document(idGrupo)
            docRef.get().addOnSuccessListener { document ->
                grupoBuscado = document.toObject(Grupo::class.java)!!
            }
            return grupoBuscado
        }

        suspend fun comprobarNombreUsuario(usuario: UsuarioActual) : Boolean {
            var tieneNombreUsuario = false
            val docRef = mFirestore.collection("Usuarios").document(usuario.email.toString())
            Log.d("Existe!", tieneNombreUsuario.toString())
            if (docRef.get().await().exists()) {
                tieneNombreUsuario = true
            }
            Log.d("Existe fuera!", tieneNombreUsuario.toString())
            return tieneNombreUsuario
        }

        suspend fun unirseAGrupo(usuarioActual: UsuarioActual, grupoElegido: Grupo) {
            val docRef = mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            val listaParticipantes = mutableListOf<String>()
            docRef.get().addOnSuccessListener { grupo ->
                    var objetoGrupo = grupo.toObject(Grupo::class.java)
                if (objetoGrupo != null) {
                        listaParticipantes.addAll(objetoGrupo.listaParticipantes!!)
                }else {
                    Log.e("Error: ", "No se encuentra el grupo")
                }
            }.await()
            listaParticipantes.add(usuarioActual.email.toString())
            docRef.update("listaParticipantes",listaParticipantes).await()
        }

        suspend fun existeGrupoPorID (idGrupo : String) : Boolean {
            var existe : Boolean = false
            val docRef = mFirestore.collection("Grupos").document(idGrupo)

            if (docRef.get().await().exists()) {
                existe = true
            }
            Log.d("Existe!", existe.toString())

            Log.d("Existe fuera!", existe.toString())
            return existe
        }

        suspend fun sacarMensajesDeGrupo(grupoElegido: Grupo): MutableList<Mensaje> {
            var listaMensajes = mutableListOf<Mensaje>()
            mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
                .collection("Mensajes").get().addOnSuccessListener { grupos ->
                    for (grupo in grupos) {
                        listaMensajes.add(grupo.toObject(Mensaje::class.java))
                    }
                }.await()
            Log.d("ListaMensajes: ", listaMensajes.toString())
            return listaMensajes
        }


        suspend fun enviarMensajeAGrupo(mensajeEnviado : String, grupoElegido : Grupo, usuarioEmisor : UsuarioActual) {
            var formato = SimpleDateFormat("HH:mm:ss")
            var hora = formato.format(Date())
            var horaField = Date()

            var mensajeAEnviar = Mensaje(usuarioEmisor.email,mensajeEnviado, horaField,usuarioEmisor.nombreUsuario)
            mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
                .collection("Mensajes").add(mensajeAEnviar).await()
            Log.d("Enviarmensaje ", "Se ha enviado el mensaje : " + mensajeEnviado)
        }

        suspend fun establecerUsuario(usuario: Usuario) {
            mFirestore.collection("Usuarios").document(usuario.email.toString()).set(usuario).await()
        }

        suspend fun sacarNombreUsuario(usuarioActual: Usuario): String? {
            var nombreUsuario = ""
            mFirestore.collection("Usuarios").document(usuarioActual.email.toString()).get().addOnSuccessListener { grupo ->
                var usuario = grupo.toObject(UsuarioActual::class.java)
                if (usuario != null) {
                    nombreUsuario = usuario.nombreUsuario.toString()
                }
            }.await()
            return nombreUsuario
        }

        suspend fun sacarUsuario(emailUsuario: String): Usuario? {
            var usuarioEncontrado = Usuario()
            mFirestore.collection("Usuarios").document(emailUsuario).get().addOnSuccessListener { user ->
                var usuario = user.toObject(Usuario::class.java)
                if (usuario != null) {
                    usuarioEncontrado = usuario
                }
            }.await()
            return usuarioEncontrado
        }

        suspend fun extraerFotoGoogle (emailUsuario: String) : String {
            var urlFotoGoogle = ""
            mFirestore.collection("Usuarios").document(emailUsuario).get().addOnSuccessListener { user ->
                var usuario = user.toObject(Usuario::class.java)
                urlFotoGoogle = usuario?.fotoPerfil.toString()
            }
            return urlFotoGoogle
        }

        suspend fun actualizarVideoElegido(grupoElegido: Grupo,nuevoVideoElegido : String) {
            var modificarRef = mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            modificarRef.update("videoElegido",nuevoVideoElegido).await()
        }

        suspend fun actualizarVideoIniciado(grupoElegido: Grupo,
                                            videoIniciado: Boolean) {
            //var modificarRef = mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            if (videoIniciado) {
                //modificarRef.update("videoIniciado",videoIniciado).await()
            }else {
                //modificarRef.update("videoIniciado",videoIniciado).await()
            }
        }

        suspend fun actualizarSegundos(grupoElegido: Grupo, seg: Float) {
            //var modificarRef = mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            //modificarRef.update("videoSegundos",seg).await()
        }

        suspend fun usuarioOnline(grupoElegido: Grupo,
                                            usuario : UsuarioActual,
                                            online: Boolean) {
            val docRef = mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
            val listaOn = mutableSetOf<String>()
            docRef.get().addOnSuccessListener { grupo ->
                var objetoGrupo = grupo.toObject(Grupo::class.java)
                if (objetoGrupo != null) {
                    listaOn.addAll(objetoGrupo.listaOnline!!)
                }else {
                    Log.e("Error: ", "No se encuentra el grupo")
                }
            }.await()
            if (online) {
                listaOn.add(usuario.email.toString())
            }else {
                listaOn.remove(usuario.email.toString())
            }
            var arrayOnline = arrayListOf<String>()
            arrayOnline.addAll(listaOn)
            docRef.update("listaOnline",arrayOnline).await()
        }


    }


}

