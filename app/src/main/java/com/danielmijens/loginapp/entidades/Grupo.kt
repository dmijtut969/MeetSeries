package com.danielmijens.loginapp.entidades

import androidx.collection.arraySetOf
import java.io.Serializable

data class Grupo(val nombreGrupo : String ?= null,
                 val categoriaGrupo : String ?= null,
                 val descripcionGrupo : String ?= null,
                 val listaParticipantes : List<String> ?= null,
                 val creador : String ?= null,
                 val idGrupo : String ?= null,
                 val fotoGrupo : String?= null,
                 val videoElegido : String ?= null,
                 var videoIniciado : Boolean ?= null,
                 val videoSegundos : Float ?= null,
                 val listaOnline : List<String> ?= arrayListOf<String>() ) : Serializable
