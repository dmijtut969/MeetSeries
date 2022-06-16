package com.danielmijens.loginapp.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.danielmijens.loginapp.entidades.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Consultas() {

    companion object {
        var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

        //Funcion en la que permite crear un grupo en Firestore.
        suspend fun  crearGrupo(
            usuarioActual: UsuarioActual,
            nombreGrupo: String,
            descripcionGrupo: String,
            fotoGrupo : String
        ) : Boolean{

            var creador = usuarioActual.email.toString()
            var idGrupo = nombreGrupo + " - " + creador
            var listaParticipantes = arrayListOf<String>(creador)

            var nuevoGrupo = Grupo(nombreGrupo,descripcionGrupo,listaParticipantes,creador,idGrupo,fotoGrupo)
            var todoCorrecto = true
            var grupoNuevoRef  = mFirestore.collection("Grupos").document(idGrupo)
            grupoNuevoRef.set(nuevoGrupo).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }.await()
            return todoCorrecto
        }

        //Funcion que crea el control del video en Firestore.
        suspend fun crearControlVideo(idGrupo: String,creador : String) : Boolean{
            var grupoNuevoRef  = mFirestore.collection("ControlVideos").document(idGrupo)
            var nuevoControlVideo = ControlVideo(idGrupo,creador,"",false,0f)
            var todoCorrecto = true
            grupoNuevoRef.set(nuevoControlVideo).addOnCompleteListener { task ->
                if (task.isCanceled) {
                    todoCorrecto = false
                }
            }.await()
            return todoCorrecto
        }

        //Funcion que permite borrar el grupo de Firestore.
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
                                borrarMensajesDeGrupo(idGrupo)
                                mFirestore.collection("Grupos").document(dc.document.id).delete()
                                Log.d("Se ha borrado", dc.document.id.toString())
                                GlobalScope.launch(Dispatchers.IO) {
                                    borrarControlVideo(idGrupo)
                                }
                            }
                        }
                    }
                })
        }

        //Funcion necesaria ya que al borrar un grupo de Firestore, primero ha que borrar los mensajes de dentro.
        fun borrarMensajesDeGrupo(idGrupito: String) {
            mFirestore.collection("Grupos").document(idGrupito)
                .collection("Mensajes").get().addOnSuccessListener { mensajes ->
                    mensajes.documents.forEach { mensaje ->
                        mensaje.reference.delete()
                    }
            }
        }

        //Funcion para salirse de grupo de Firestore.
        fun salirseDeGrupo (usuarioActual: UsuarioActual,
                         grupoElegido: Grupo) {
            var listaParticipantesNueva = arrayListOf<String>()
            listaParticipantesNueva.addAll(grupoElegido.listaParticipantes!!)
            for (participante in grupoElegido.listaParticipantes!!) {
                if (participante.equals(usuarioActual.email)) {
                    listaParticipantesNueva.remove(participante)
                }
            }
            mFirestore.collection("Grupos").document(grupoElegido.idGrupo!!).update("listaParticipantes",
                listaParticipantesNueva)
            Log.d("Actualizado", "listaparticipantes")
        }

        //Funcion que borra el control del video en Firestore.
        fun borrarControlVideo (idGrupo: String) {
            mFirestore.collection("ControlVideos").whereEqualTo("idGrupo",idGrupo)
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
                                mFirestore.collection("ControlVideos").document(dc.document.id).delete()
                                Log.d("Se ha borrado", dc.document.id.toString())
                            }
                        }
                    }
                })
        }

        //Funcion de utilidad para comprobar si el usuario tiene nombre de usuario.
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

        //Funcion que permite unirse a un grupo ya creado en Firestore.
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
            try {
                docRef.update("listaParticipantes",listaParticipantes).await()
            }catch (e : FirebaseFirestoreException) {
                Log.e("Error en unirseAGrupo ",e.toString())
            }

        }

        //Funcion de utilidad que comprueba si ya existe el grupo con esa id.
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

        //Funcion para enviar mensaje al grupo en el que se esta.
        suspend fun enviarMensajeAGrupo(mensajeEnviado : String, grupoElegido : Grupo, usuarioEmisor : UsuarioActual) {
            var horaField = Date()
            var mensajeAEnviar = Mensaje(usuarioEmisor.email,mensajeEnviado, horaField,usuarioEmisor.nombreUsuario)
            mFirestore.collection("Grupos").document(grupoElegido.idGrupo.toString())
                .collection("Mensajes").add(mensajeAEnviar).await()
            Log.d("Enviarmensaje ", "Se ha enviado el mensaje : " + mensajeEnviado)
        }

        //Funcion para establecer el nuevo usuario en Firestore.
        suspend fun establecerUsuario(usuario: Usuario) {
            mFirestore.collection("Usuarios").document(usuario.email.toString()).set(usuario).await()
        }

        //Funcion de utilidad para traer el nombre de usuario del usuario actual.
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

        //Funcion de utilidad para traer atraves del email de usuario, el usuario en concreto.
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

        //Funcion para actualizar en nuevo video elegido en el control de video.
        suspend fun actualizarVideoElegido(control: ControlVideo,nuevoVideoElegido : String) {
            var modificarRef = mFirestore.collection("ControlVideos").document(control.idGrupo.toString())
            modificarRef.update("videoElegido",nuevoVideoElegido).await()

        }

        //Funcion para actualizar si el video esta inicido en el control de video.
        suspend fun actualizarVideoIniciado(control: ControlVideo,
                                            videoIniciado: Boolean) {
            var modificarRef = mFirestore.collection("ControlVideos").document(control.idGrupo.toString())
            if (videoIniciado) {
                modificarRef.update("videoIniciado",videoIniciado).await()
            }else {
                modificarRef.update("videoIniciado",videoIniciado).await()
            }
            Log.d("videoIniciado control " , videoIniciado.toString())
        }

        //Funcion para actualizar los segundos del video en el control de video.
        suspend fun actualizarSegundos(control: ControlVideo, seg: Float) {
            var modificarRef = mFirestore.collection("ControlVideos").document(control.idGrupo.toString())
            modificarRef.update("videoSegundos",seg).await()
        }

        //Funcion que detecta cuando el usuario entra en un grupo o sale, para actualizar si esta online o no.
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
            try {
                docRef.update("listaOnline",arrayOnline).await()
            }catch (e : FirebaseFirestoreException) {
                Log.e("Error en usuarioOnline ",e.toString())
            }
        }

        //Funcion de utilizar para buscar el control de video por ID.
        suspend fun buscarControlVideoPorID (idGrupo : String) : ControlVideo {
            var controlVideoBuscado : ControlVideo = ControlVideo()
            val docRef = mFirestore.collection("ControlVideos").document(idGrupo)
            docRef.get().addOnSuccessListener { document ->
                try {
                    controlVideoBuscado = document.toObject(ControlVideo::class.java)!!
                }catch (e : NullPointerException) {
                    Log.e("Null pointer en ","buscarControlVideoPorID()")
                }
            }.await()
            return controlVideoBuscado
        }


    }


}

