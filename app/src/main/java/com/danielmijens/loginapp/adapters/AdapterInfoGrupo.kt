package com.danielmijens.loginapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentInfoGrupoBinding
import com.danielmijens.loginapp.databinding.ItemUsuarioBinding
import com.danielmijens.loginapp.entidades.Usuario
import com.danielmijens.loginapp.firebase.Storage
import com.danielmijens.loginapp.fragments.InfoGrupoFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterInfoGrupo(
    var binding: FragmentInfoGrupoBinding
    , var listaUsuarios: MutableList<Usuario>
    , var misGruposFragment: InfoGrupoFragment
    , var toolbar: Toolbar
) : RecyclerView.Adapter<AdapterInfoGrupo.AdapterInfoGrupoViewHolder>() {
    class AdapterInfoGrupoViewHolder (val binding: ItemUsuarioBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInfoGrupoViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterInfoGrupoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterInfoGrupoViewHolder, position: Int) {
        var usuario : Usuario = listaUsuarios[position]
        Log.d("AdapterInfoGrupo Dani", usuario.toString())
        holder.binding.nombreItemUsuarioTextView.text = usuario.nombreUsuario

        //Si el usuario tiene foto de perfil, que no sea de Google, saldra en la información.
        GlobalScope.launch(Dispatchers.IO) {
            var uriFotoElegido = Storage.extraerImagenPerfil(usuario.email.toString())
                withContext(Dispatchers.Main) {
                    if (uriFotoElegido.toString().isNullOrEmpty()) {
                        holder.binding.fotoUsuariomageView.visibility = View.GONE
                    }else {
                        Picasso.get().load(uriFotoElegido).into(holder.binding.fotoUsuariomageView)
                        holder.binding.fotoUsuariomageView.visibility = View.VISIBLE
                    }
                }
        }
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    //Para filtrar con el searchView
    fun setFilter(filtrarUsuarios: ArrayList<Usuario>) {
        listaUsuarios = filtrarUsuarios
        notifyDataSetChanged()
    }


}