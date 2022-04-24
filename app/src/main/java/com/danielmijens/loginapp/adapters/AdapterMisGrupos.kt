package com.danielmijens.loginapp.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.MisGruposFragment
import com.danielmijens.loginapp.OnFragmentListener
import com.danielmijens.loginapp.R
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.danielmijens.loginapp.entidades.Grupo
import com.danielmijens.loginapp.entidades.UsuarioActual
import com.danielmijens.loginapp.firebase.Consultas
import com.danielmijens.loginapp.firebase.Storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterMisGrupos(
    var binding: FragmentMisGruposBinding
    , var listaGrupos: ArrayList<Grupo>
    , var usuarioActual: UsuarioActual
    , var misGruposFragment: MisGruposFragment
    , var toolbar: androidx.appcompat.widget.Toolbar
) : RecyclerView.Adapter<AdapterMisGrupos.AdapterMisGruposViewHolder>() {
    class AdapterMisGruposViewHolder (val binding: ItemGrupoBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    private lateinit var listener : OnFragmentListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMisGruposViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterMisGruposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMisGruposViewHolder, position: Int) {
        var grupo : Grupo = listaGrupos[position]
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

        holder.binding.itemGrupoLinearLayout.setOnClickListener() {
            misGruposFragment.listener.onElegirGrupoClick(usuarioActual,grupo,toolbar)
            true
        }

        holder.binding.itemGrupoLinearLayout.setOnLongClickListener {
            showDialogAlertSimple(grupo)
            true
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
                    misGruposFragment.refrescarRecycler()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .show()
    }

    override fun getItemCount(): Int {
        return listaGrupos.size
    }


}