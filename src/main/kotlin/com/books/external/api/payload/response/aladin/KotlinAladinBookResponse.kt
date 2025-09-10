package com.books.external.api.payload.response.aladin

data class KotlinAladinBookResponse(
    val version: String,
    val logo: String,
    val title: String,
    val link: String,
    val pubDate: String,
    val totalResults: Long,
    val startIndex: Long,
    val query: String,
    val searchCategoryId: Long,
    val searchCategoryName: String,
    val item: List<KotlinAladinSearchResponse>
)