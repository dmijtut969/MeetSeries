package com.danielmijens.loginapp.entidades

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Mensaje(

    val emisor: String ?= null,
    val mensaje: String ?= null,
    @ServerTimestamp
    val hora: Date?= null,
    val nombreUsuarioEmisor : String ?= null) : Serializable