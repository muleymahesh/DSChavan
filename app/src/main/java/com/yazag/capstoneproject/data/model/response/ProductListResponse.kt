package com.yazag.capstoneproject.data.model.response

data class ProductListResponse(
    val data: List<Data>,
    val responseCode: Int,
    val result: String
)