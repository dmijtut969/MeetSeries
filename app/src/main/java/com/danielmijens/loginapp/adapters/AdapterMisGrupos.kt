package com.danielmijens.loginapp.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.fragments.MisGruposFragment
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
    , var drawer: DrawerLayout?= null
) : RecyclerView.Adapter<AdapterMisGrupos.AdapterMisGruposViewHolder>() {
    class AdapterMisGruposViewHolder (val binding: ItemGrupoBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMisGruposViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterMisGruposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMisGruposViewHolder, position: Int) {
        var grupo : Grupo = listaGrupos[position]
        holder.binding.nombreItemGrupoTextView.text = grupo.nombreGrupo
        holder.binding.contadorPersonasTextView.text = grupo.listaParticipantes?.size.toString() + " participantes"
        Log.d("grupo.nombreGrupo " ,grupo.nombreGrupo.toString())
        Log.d("grupo.videoIniciado " ,grupo.videoIniciado.toString())

        //Con este condicional controlamos que se muestre si el video esta iniciado o no
        if (grupo.videoIniciado == null || grupo.videoIniciado == false) {
            holder.binding.videoPlaying.visibility = View.INVISIBLE
        }else {
            holder.binding.videoPlaying.visibility = View.VISIBLE
        }

        //Lanzamos una corutina para que vaya trayendo las fotos de los grupos.
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

        //Al elegir un grupo nos mandara a su pestaña.
        holder.binding.itemGrupoLinearLayout.setOnClickListener() {
            misGruposFragment.listener.onElegirGrupoClick(usuarioActual,grupo,toolbar)
            drawer?.findViewById<View>(R.id.nav_logOut)?.visibility = View.GONE
            true
        }

        //Si el usuario mantiene pulsado, si es el creador podra borrar el grupo, y si es un usuario corriente podra salirse.
        holder.binding.itemGrupoLinearLayout.setOnLongClickListener {
            if (usuarioActual.email==grupo.creador) {
                borrarGrupoDialog(grupo)
            }else {
                salirseDeGrupo(grupo)
            }
            true
        }
    }

    //Funcion para borrar el grupo elegido.
    fun borrarGrupoDialog(grupo: Grupo) {
        AlertDialog.Builder(misGruposFragment.context)
            .setTitle("¿Quiere borrar el siguiente grupo?")
            .setMessage(grupo.nombreGrupo)
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

    //Funcion para salirse del grupo elegido.
    fun salirseDeGrupo(grupo: Grupo) {
        AlertDialog.Builder(misGruposFragment.context)
            .setTitle("¿Quiere salir del siguiente grupo?")
            .setMessage(grupo.nombreGrupo)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    Consultas.salirseDeGrupo(usuarioActual,grupo)
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

    //Utilidad para el filtro del searchView.
    fun setFilter(filterString: ArrayList<Grupo>) {
        listaGrupos = filterString
        notifyDataSetChanged()
    }


}