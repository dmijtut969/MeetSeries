package com.danielmijens.loginapp

interface OnFragmentListener {
    fun onCrearGrupoClick(nuevoNombreGrupo : String,nuevaDescripcionGrupo : String)
    fun onElegirCategoria()
    fun onBuscarClick(campo : String,valorABuscar : String)
    fun actualizarRecyclerMisGrupos()
}