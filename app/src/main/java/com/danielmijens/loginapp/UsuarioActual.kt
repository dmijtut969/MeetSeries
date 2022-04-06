package com.danielmijens.loginapp

import android.media.Image
import android.widget.ImageView
import java.io.Serializable

data class UsuarioActual(var email: String ?= null, var fotoPerfil: String ?= null, var nombreUsuario : String ?= null) : Serializable

