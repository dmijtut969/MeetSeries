package com.danielmijens.loginapp

import java.io.Serializable

data class Grupo(val nombreGrupo : String ?= null,val categoriaGrupo : String ?= null, val fotoGrupo : Int ?= null ) : Serializable
