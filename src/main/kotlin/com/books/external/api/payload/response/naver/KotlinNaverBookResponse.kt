package com.books.external.api.payload.response.naver

data class KotlinNaverBookResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<KotlinNaverSearchResponse>
)