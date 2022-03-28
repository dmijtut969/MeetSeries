package com.danielmijens.loginapp.firebase

import android.net.Uri
import com.danielmijens.loginapp.UsuarioActual
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

abstract class Storage {
    companion object {
        val storage : FirebaseStorage = Firebase.storage

        suspend fun extraerImagenPerfil(usuarioActual: UsuarioActual) : Uri {
            return storage.reference.child("fotoPerfil/logo_hnos.jpg").downloadUrl.await()
        }

        suspend fun elegirVideo(usuarioActual: UsuarioActual) : Uri {
            return storage.reference.child("videos/prueba_video.mp4").downloadUrl.await()
        }
    }
}