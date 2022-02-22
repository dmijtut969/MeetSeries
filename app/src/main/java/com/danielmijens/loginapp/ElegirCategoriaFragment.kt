package com.danielmijens.loginapp

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.databinding.FragmentElegirCategoriaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ElegirCategoriaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ElegirCategoriaFragment(var usuarioActual: UsuarioActual,var nuevoNombreGrupo: String, var nuevaDescripcionGrupo: String) : Fragment(),SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    lateinit var binding : FragmentElegirCategoriaBinding
    private lateinit var adapter : AdapterElegirCategoria
    private val categoriasImages = mutableListOf<String>()
    private val categoriasTitulos = mutableListOf<String>()
    lateinit var listener : OnFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentElegirCategoriaBinding.inflate(layoutInflater)
        binding.searchViewCategoria.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = AdapterElegirCategoria(categoriasImages,categoriasTitulos,this,nuevoNombreGrupo,nuevaDescripcionGrupo,usuarioActual)
        binding.recyclerViewCategorias.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCategorias.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


    private fun getRetrofit():Retrofit  {
        return Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByCategoria(query : String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Entro en coroutine","Entro en coroutine")
            var call : Response<CategoriaResponse> = getRetrofit().create(ApiService::class.java).getCategoriaPorNombre("anime?q=$query")
            Log.d("Paso call en coroutine","Paso call en coroutine")
            val categorias = call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    categoriasImages.clear()
                    categorias?.dataAnime?.forEach {
                        data ->
                        categoriasImages.add(data.images.webp.image_url)
                        categoriasTitulos.add(data.title)
                    }
                    adapter.notifyDataSetChanged()
                }else {
                    Toast.makeText(context,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
                }
                ocultarTeclado()
            }

        }
    }

    private fun ocultarTeclado() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            binding.animationViewEsperando.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.recyclerViewCategorias.visibility = View.VISIBLE
            searchByCategoria(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentListener) {
            listener = context
        }
    }
}