package com.danielmijens.loginapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielmijens.loginapp.databinding.FragmentMisGruposBinding
import com.danielmijens.loginapp.databinding.ItemGrupoBinding

class AdapterMisGrupos(var binding: FragmentMisGruposBinding
, var listaGrupos : ArrayList<Grupo>
,var usuarioActual: UsuarioActual
,var misGruposFragment: MisGruposFragment) : RecyclerView.Adapter<AdapterMisGrupos.AdapterMisGruposViewHolder>() {
    class AdapterMisGruposViewHolder (val binding: ItemGrupoBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    private lateinit var listener : OnFragmentListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMisGruposViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterMisGruposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMisGruposViewHolder, position: Int) {
        val grupo : Grupo = listaGrupos[position]
        holder.binding.nombreItemGrupoTextView.text = grupo.nombreGrupo
        holder.binding.logoDelGrupoImageView.setImageResource(0)

        holder.binding.itemGrupoLinearLayout.setOnLongClickListener() {
            showDialogAlertSimple(grupo)
            true
        }
        //holder.binding.imageButtonEditar.setOnClickListener() {
        //    var intent = Intent(pedidosActivity,EditarPedidoActivity::class.java)
        //    intent.putExtra("pedido",pedido)
        //    intent.putExtra("listapedidos",listaGrupos)
        //    intent.putExtra("indice",position)
        //    pedidosActivity.startActivity(intent)
        //}
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