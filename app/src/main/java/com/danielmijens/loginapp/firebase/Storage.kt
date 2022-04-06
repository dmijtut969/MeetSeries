package com.danielmijens.loginapp.firebase

import android.net.Uri
import android.util.Log
import com.danielmijens.loginapp.UsuarioActual
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

abstract class Storage {
    companion object {
        val storage : FirebaseStorage = Firebase.storage

        suspend fun extraerImagenPerfil(usuarioActual: UsuarioActual) : Uri {
            var uri : Uri = Uri.EMPTY
            try {
                uri = storage.reference.child("fotoPerfil/" + usuarioActual.email).downloadUrl.await()
            }catch (e : StorageException) {
                Log.d("Ha saltado excepcion","No tiene foto de perfil")
            }
            return uri
        }

        suspend fun subirImagenPerfil(usuarioActual: UsuarioActual, imagen: Uri)  {

            val uploadTask = storage.reference.child("fotoPerfil/" + usuarioActual.email).putFile(imagen)
            uploadTask.addOnFailureListener {
                Log.d("Ha ocurrido un error: "," Ha fallado la subida de imagen")
            }.addOnSuccessListener { taskSnapshot ->
                Log.d("Ha ido todo bien: ","Se ha subido la imagen")
            }.await()
        }



        suspend fun elegirVideo(usuarioActual: UsuarioActual) : Uri {
            return storage.reference.child("videos/prueba_video.mp4").downloadUrl.await()
        }

    }
}