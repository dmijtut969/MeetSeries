package com.danielmijens.loginapp

import com.google.gson.annotations.SerializedName

data class CategoriaResponse(@SerializedName("status") var status: String,@SerializedName("message") var animes : List<String>)
