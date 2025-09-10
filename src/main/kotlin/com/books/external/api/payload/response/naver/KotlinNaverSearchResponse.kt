package com.books.external.api.payload.response.naver

data class KotlinNaverSearchResponse(
    val title: String,
    val image: String,
    val author: String,
    val price: String,
    val discount: String,
    val publisher: String,
    val pubdate: String,
    val isbn: String,
    val description: String
)