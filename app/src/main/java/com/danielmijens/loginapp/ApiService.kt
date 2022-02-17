package com.danielmijens.loginapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getCategoriaPorNombre(@Url url : String) :Response<CategoriaResponse>
}