package com.danielmijens.loginapp.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.R
import com.danielmijens.loginapp.databinding.FragmentBusquedaBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.danielmijens.loginapp.fragments.BusquedaFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterBusqueda(var binding: FragmentBusquedaBinding
                      , var listaGruposBusqueda : ArrayList<Grupo>
                      , var usuarioActual: UsuarioActual
                      , var busquedaFragment: BusquedaFragment
) : RecyclerView.Adapter<AdapterBusqueda.AdapterBusquedaViewHolder>() {
    class AdapterBusquedaViewHolder (val binding: ItemGrupoBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    private lateinit var listener : OnFragmentListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterBusquedaViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterBusquedaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterBusquedaViewHolder, position: Int) {
        val grupo : Grupo = listaGruposBusqueda[position]
        holder.binding.nombreItemGrupoTextView.text = grupo.nombreGrupo
        holder.binding.categoriaItemGrupoTextView.text = grupo.categoriaGrupo

        GlobalScope.launch(Dispatchers.IO) {
            var uriFotoElegido = Storage.extraerImagenGrupo(grupo.idGrupo)
            withContext(Dispatchers.Main) {
                if (uriFotoElegido.toString().isNullOrEmpty()) {
                    holder.binding.fotoGrupoImageView.setImageResource(R.drawable.icono_meet)
                }else {
                    Picasso.get().load(uriFotoElegido).into(holder.binding.fotoGrupoImageView)
                }
            }
        }

        holder.binding.itemGrupoLinearLayout.setOnClickListener {
            showDialogAlertSimple(grupo)
            true
        }

        if (grupo.videoIniciado == null || grupo.videoIniciado == false) {
            holder.binding.videoPlaying.visibility = View.INVISIBLE
        }else {
            holder.binding.videoPlaying.visibility = View.VISIBLE
        }
    }

    fun showDialogAlertSimple(grupo: Grupo) {
            AlertDialog.Builder(busquedaFragment.context)
                .setTitle("Ha elegido el grupo grupo para unirse")
                .setMessage("¿Esta seguro?")
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, which ->
                        GlobalScope.launch(Dispatchers.IO) {
                            Consultas.unirseAGrupo(usuarioActual, grupo)
                            busquedaFragment.listener.onVerMisGruposClick()
                        }
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->

                    })
                .show()
    }

    override fun getItemCount(): Int {
        return listaGruposBusqueda.size
    }

    fun setFilter(filterString: ArrayList<Grupo>) {
        listaGruposBusqueda = filterString
        notifyDataSetChanged()
    }


}