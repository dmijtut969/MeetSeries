package com.danielmijens.loginapp.entidades

import java.io.Serializable

data class UsuarioActual(var email: String ?= null, var fotoPerfil: String ?= null, var nombreUsuario : String ?= null) : Serializable

