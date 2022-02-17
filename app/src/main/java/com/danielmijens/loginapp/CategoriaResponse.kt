package com.danielmijens.loginapp

import com.danielmijens.loginapp.infoApiAnimes.Data
import com.danielmijens.loginapp.infoApiAnimes.Pagination
import com.google.gson.annotations.SerializedName

data class CategoriaResponse(@SerializedName("pagination") var pagination: Pagination,@SerializedName("data") var dataAnime : List<Data>)
