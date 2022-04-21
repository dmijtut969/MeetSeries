package com.danielmijens.loginapp.entidades

import java.io.Serializable

data class Mensaje(

    val emisor: String ?= null,
    val mensaje: String ?= null,
    val hora: String ?= null,
    val nombreUsuarioEmisor : String ?= null) : Serializable