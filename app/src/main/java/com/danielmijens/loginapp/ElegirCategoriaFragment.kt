package com.danielmijens.loginapp

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.INPUT_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielmijens.loginapp.databinding.FragmentElegirCategoriaBinding
import com.squareup.okhttp.Dispatcher
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
class ElegirCategoriaFragment : Fragment(),SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentElegirCategoriaBinding
    private lateinit var adapter : AdapterElegirCategoria
    private val categoriasImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentElegirCategoriaBinding.inflate(layoutInflater)
        binding.searchViewCategoria.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = AdapterElegirCategoria(categoriasImages)
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
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByCategoria(query : String) {
        CoroutineScope(Dispatchers.IO).launch {
            var call : Response<CategoriaResponse> = getRetrofit().create(ApiService::class.java).getCategoriaPorNombre("$query/images")
            val categorias = call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    val images = categorias?.animes ?: emptyList()
                    categoriasImages.clear()
                    categoriasImages.addAll(images)
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
            searchByCategoria(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}