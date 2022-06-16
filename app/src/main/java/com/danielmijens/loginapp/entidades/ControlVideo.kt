package com.danielmijens.loginapp.entidades

import java.io.Serializable

data class ControlVideo(
    val idGrupo : String ?= null,
    val creador : String ?= null,
    var videoElegido : String ?= null,
    var videoIniciado : Boolean ?= null,
    val videoSegundos : Float ?= 0f) : Serializable
