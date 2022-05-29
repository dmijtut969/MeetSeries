package com.danielmijens.loginapp.entidades

import androidx.collection.arraySetOf
import java.io.Serializable

data class ControlVideo(//val nombreGrupo : String ?= null,
                        //val listaParticipantes : List<String> ?= null,
                        val idGrupo : String ?= null,
                        val creador : String ?= null,
                        val videoElegido : String ?= null,
                        val videoIniciado : Boolean ?= null,
                        val videoSegundos : Float ?= null) : Serializable
