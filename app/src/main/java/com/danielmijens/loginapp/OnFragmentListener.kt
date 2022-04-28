package com.danielmijens.loginapp

import androidx.appcompat.widget.Toolbar
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual

interface OnFragmentListener {
    fun onCrearGrupoClick(grupoElegido  : Grupo)
    fun onElegirGrupoClick(usuarioActual: UsuarioActual, grupo: Grupo, toolbar: androidx.appcompat.widget.Toolbar)
    fun onElegirCategoria()
    fun onBuscarClick(campo : String,valorABuscar : String)
    fun onVerMisGruposClick()
    fun actualizarRecyclerMisGrupos()
    fun onVerInfoGrupo(grupoElegido: Grupo, toolbar: Toolbar)
}