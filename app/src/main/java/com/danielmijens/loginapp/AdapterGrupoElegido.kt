package com.danielmijens.loginapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.danielmijens.loginapp.databinding.ItemMensajeBinding
import com.danielmijens.loginapp.firebase.Storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterGrupoElegido(
    var binding: FragmentGrupoElegidoBinding
    , var listaMensajes: MutableList<Mensaje>
    , var usuarioActual: UsuarioActual
    , var misGruposFragment: GrupoElegidoFragment) : RecyclerView.Adapter<AdapterGrupoElegido.AdapterGrupoElegidoViewHolder>() {
    class AdapterGrupoElegidoViewHolder (val binding: ItemMensajeBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    private lateinit var listener : OnFragmentListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterGrupoElegidoViewHolder {
        val binding = ItemMensajeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterGrupoElegidoViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AdapterGrupoElegidoViewHolder, position: Int) {
        val mensajeRecibido : Mensaje = listaMensajes[position]
        if (mensajeRecibido.nombreUsuarioEmisor.isNullOrEmpty()) {
            holder.binding.textViewNombreUsuario.text = mensajeRecibido.emisor
        }else {
            holder.binding.textViewNombreUsuario.text = mensajeRecibido.nombreUsuarioEmisor
        }

        holder.binding.mensajeTextView.text = mensajeRecibido.mensaje
        holder.binding.fechaMensaje.text = mensajeRecibido.hora.toString()
        GlobalScope.launch (Dispatchers.IO){
            withContext(Dispatchers.Main) {
                var fotoAMostrar = Storage.extraerImagenPerfil(mensajeRecibido.emisor.toString())
                if (fotoAMostrar.toString().isNullOrEmpty()) {
                    holder.binding.imageViewFotoUsuario.setImageResource(R.drawable.icono_meet)
                }else {
                    Picasso.get().load(fotoAMostrar).into(holder.binding.imageViewFotoUsuario)
                }

            }
        }

    }

    fun showDialogAlertSimple(grupo: Grupo) {
        AlertDialog.Builder(misGruposFragment.context)
            .setTitle("Va a borrar un grupo")
            .setMessage("¿Esta seguro?")
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    Consultas.borrarGrupo(usuarioActual,grupo.nombreGrupo.toString())
                    Thread.sleep(1000)
                    //misGruposFragment.refrescarRecycler()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .show()
    }

    override fun getItemCount(): Int {
        return listaMensajes.size
    }


}