package com.danielmijens.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.ItemSerieBinding
import com.squareup.picasso.Picasso

class AdapterElegirCategoria(
    val categoriasImagenes: MutableList<String>,
    val categoriasTitulos: MutableList<String>
) :
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
        val categoriaImagen : String = categoriasImagenes[position]
        val categoriaTitulo : String = categoriasTitulos[position]
        Picasso.get().load(categoriaImagen).into(holder.binding.imageViewSerie)
        holder.binding.textViewNombreSerie.text = categoriaTitulo
    }

    override fun getItemCount(): Int {
       return categoriasImagenes.size
    }
}