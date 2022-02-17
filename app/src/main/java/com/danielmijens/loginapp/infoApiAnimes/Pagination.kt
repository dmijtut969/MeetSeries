package com.danielmijens.loginapp.infoApiAnimes

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int
)
