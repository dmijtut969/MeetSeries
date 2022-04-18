package com.danielmijens.loginapp

interface OnFragmentListener {
    fun onCrearGrupoClick(nuevoNombreGrupo : String,nuevaDescripcionGrupo : String)
    fun onElegirGrupoClick(usuarioActual: UsuarioActual, grupo: Grupo, toolbar: androidx.appcompat.widget.Toolbar)
    fun onElegirCategoria()
    fun onBuscarClick(campo : String,valorABuscar : String)
    fun onVerMisGruposClick()
    fun actualizarRecyclerMisGrupos()

}