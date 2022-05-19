package com.danielmijens.loginapp.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.GrupoElegidoFragment
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.R
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.danielmijens.loginapp.databinding.ItemMensajeBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.Mensaje
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class AdapterGrupoElegido(
    var binding: FragmentGrupoElegidoBinding
    , var listaMensajes: MutableList<Mensaje>
    , var usuarioActual: UsuarioActual
    , var misGruposFragment: GrupoElegidoFragment
) : RecyclerView.Adapter<AdapterGrupoElegido.AdapterGrupoElegidoViewHolder>() {
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
        holder.binding.linearLayoutMiMensaje.setOnClickListener {
            if (holder.binding.fechaMensaje.visibility == View.INVISIBLE) {
                holder.binding.fechaMensaje.visibility = View.VISIBLE

            }else {
                holder.binding.fechaMensaje.visibility = View.INVISIBLE

            }

        }
        /*GlobalScope.launch (Dispatchers.IO){
            withContext(Dispatchers.Main) {
                var fotoAMostrar = Storage.extraerImagenPerfil(mensajeRecibido.emisor.toString())
                if (fotoAMostrar.toString().isNullOrEmpty()) {
                    holder.binding.imageViewFotoUsuario.setImageResource(R.drawable.icono_meet)
                }else {
                    Picasso.get().load(fotoAMostrar).into(holder.binding.imageViewFotoUsuario)
                }

            }
        }*/
    }

    override fun getItemCount(): Int {
        return listaMensajes.size
    }


}