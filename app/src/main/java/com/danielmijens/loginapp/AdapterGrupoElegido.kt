package com.danielmijens.loginapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentGrupoElegidoBinding
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.danielmijens.loginapp.databinding.ItemMensajeBinding

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
        if (mensajeRecibido.emisor == usuarioActual.email.toString()) {
            holder.binding.otrosMensajesLayout.visibility = View.GONE
            holder.binding.miMensajeTextView.text = mensajeRecibido.mensaje
            holder.binding.fechaMiMensaje.text = mensajeRecibido.hora.toString()
        }else {
            holder.binding.misMensajesLayout.visibility = View.GONE
            holder.binding.otroMensajeTextView.text = mensajeRecibido.mensaje
            holder.binding.fechaOtroMensaje.text = mensajeRecibido.hora.toString()
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