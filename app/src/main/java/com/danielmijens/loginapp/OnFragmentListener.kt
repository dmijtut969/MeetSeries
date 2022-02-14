package com.danielmijens.loginapp

interface OnFragmentListener {
    fun onCrearGrupoClick()
    fun onBuscarClick(campo : String,valorABuscar : String)
    fun actualizarRecyclerMisGrupos()
}