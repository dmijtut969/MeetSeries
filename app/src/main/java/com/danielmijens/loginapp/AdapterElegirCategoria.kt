package com.danielmijens.loginapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.ItemSerieBinding
import com.squareup.picasso.Picasso

class AdapterElegirCategoria(
    val categoriasImagenes: MutableList<String>,
    val categoriasTitulos: MutableList<String>,
    var elegirCategoriaFragment: ElegirCategoriaFragment,
    var nuevoNombreGrupo: String,
    var nuevaDescripcionGrupo: String,
    var usuarioActual: UsuarioActual,

) :
    RecyclerView.Adapter<AdapterElegirCategoria.AdapterElegirCategoriaViewHolder>() {
    lateinit var categoriaElegida : String
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

        holder.binding.itemSerieLinearLayout.setOnClickListener {
            categoriaElegida = categoriaTitulo
            showAlert(holder.binding)
        }
    }

    override fun getItemCount(): Int {
       return categoriasImagenes.size
    }

    fun showAlert(binding: ItemSerieBinding) {
        AlertDialog.Builder(elegirCategoriaFragment.context)
            .setTitle("Va a crear un grupo con la categoria :")
            .setMessage(categoriaElegida)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    Consultas.crearGrupo(usuarioActual,nuevoNombreGrupo,nuevaDescripcionGrupo,categoriaElegida)
                    elegirCategoriaFragment.binding.recyclerViewCategorias.visibility = View.GONE
                    elegirCategoriaFragment.binding.animationViewEsperando.visibility = View.VISIBLE
                    elegirCategoriaFragment.listener.onElegirCategoria()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->

                })
            .show()
    }

}