package com.danielmijens.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.google.android.material.snackbar.Snackbar

class AdapterMisGrupos(var binding: FragmentMisGruposBinding, var listaGrupos : ArrayList<Grupo>) : RecyclerView.Adapter<AdapterMisGrupos.AdapterMisGruposViewHolder>() {
    class AdapterMisGruposViewHolder (val binding: ItemGrupoBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMisGruposViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterMisGruposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMisGruposViewHolder, position: Int) {
        val grupo : Grupo = listaGrupos[position]
        holder.binding.nombreItemGrupoTextView.text = grupo.nombreGrupo
        holder.binding.logoDelGrupoImageView.setImageResource(0)

        holder.binding.itemGrupoLinearLayout.setOnClickListener {
            Snackbar.make(binding.root, "Aqui se mostraran los grupos del usuario", Snackbar.LENGTH_SHORT).show()
        }
        //holder.binding.imageButtonEditar.setOnClickListener() {
        //    var intent = Intent(pedidosActivity,EditarPedidoActivity::class.java)
        //    intent.putExtra("pedido",pedido)
        //    intent.putExtra("listapedidos",listaGrupos)
        //    intent.putExtra("indice",position)
        //    pedidosActivity.startActivity(intent)
        //}
    }

    override fun getItemCount(): Int {
        return listaGrupos.size
    }
}