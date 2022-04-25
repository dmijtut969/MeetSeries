package com.danielmijens.loginapp.entidades

import android.media.Image
import android.widget.ImageView
import java.io.Serializable

data class Usuario(var email: String ?= null,
                   var fotoPerfil: String ?= null,
                   var nombreUsuario : String ?= null,
                   var edad : String ?= null,
                   var serieFav : String ?= null) : Serializable

