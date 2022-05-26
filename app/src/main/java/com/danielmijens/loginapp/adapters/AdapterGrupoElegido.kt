package com.danielmijens.loginapp.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Layout
import android.text.format.DateUtils
import android.view.Gravity
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

        //Mensaje derecha e izquierda
        if (usuarioActual.email==mensajeRecibido.emisor) {
            holder.binding.linearLayoutTotal.gravity = Gravity.RIGHT
            holder.binding.textViewNombreUsuario.visibility = View.GONE
        }else {
            holder.binding.linearLayoutTotal.gravity = Gravity.LEFT
            holder.binding.textViewNombreUsuario.visibility = View.VISIBLE
        }
        if (mensajeRecibido.nombreUsuarioEmisor.isNullOrEmpty()) {
            holder.binding.textViewNombreUsuario.text = mensajeRecibido.emisor
        }else {
            holder.binding.textViewNombreUsuario.text = mensajeRecibido.nombreUsuarioEmisor
        }
        holder.binding.mensajeTextView.text = mensajeRecibido.mensaje
        var fecha = mensajeRecibido.hora.toString()
        DateUtils.isToday(mensajeRecibido.hora!!.time)
        var hora = fecha.substring(fecha.indexOf(":")-2,fecha.indexOf(":")+6)
        if (DateUtils.isToday(mensajeRecibido.hora!!.time)) {
            hora = "Hoy - " + hora
        }
        holder.binding.fechaMensaje.text = hora
        holder.binding.linearLayoutTotal.setOnClickListener {
            if (holder.binding.fechaMensaje.visibility == View.GONE) {
                holder.binding.fechaMensaje.visibility = View.VISIBLE
                if (usuarioActual.email==mensajeRecibido.emisor) {
                    holder.binding.mensajeTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                }else {
                    holder.binding.mensajeTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
            }else {
                holder.binding.fechaMensaje.visibility = View.GONE
              //  holder.binding.mensajeTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
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