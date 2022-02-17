package com.danielmijens.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.ItemGrupoBinding
import com.danielmijens.loginapp.databinding.ItemSerieBinding
import com.squareup.picasso.Picasso

class AdapterElegirCategoria(val categorias: List<String>) :
    RecyclerView.Adapter<AdapterElegirCategoria.AdapterElegirCategoriaViewHolder>() {
    class AdapterElegirCategoriaViewHolder (val binding: ItemSerieBinding) : RecyclerView.ViewHolder(binding.root) {

}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterElegirCategoriaViewHolder {
        val binding = ItemSerieBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterElegirCategoria.AdapterElegirCategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterElegirCategoriaViewHolder, position: Int) {
        val categoriaSerie : String = categorias[position]
        Picasso.get().load(categoriaSerie).into(holder.binding.imageViewSerie)
    }

    override fun getItemCount(): Int {
       return categorias.size
    }
}