package com.danielmijens.loginapp.entidades

import java.io.Serializable

data class Grupo(val nombreGrupo : String ?= null,
                 val categoriaGrupo : String ?= null,
                 val descripcionGrupo : String ?= null,
                 val listaParticipantes : List<String> ?= null,
                 val creador : String ?= null,
                 val idGrupo : String ?= null) : Serializable
