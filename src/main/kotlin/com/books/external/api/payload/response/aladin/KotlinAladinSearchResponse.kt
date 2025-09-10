package com.books.external.api.payload.response.aladin

data class KotlinAladinSearchResponse(
    val title: String,
    val author: String,
    val pubDate: String,
    val description: String,
    val isbn: String,
    val isbn13: String,
    val itemId: String,
    val priceSales: String,
    val priceStandard: String,
    val mallType: String,
    val stockStatus: String,
    val cover: String,
    val categoryId: Long,
    val categoryName: String,
    val publisher: String,
    val customerReviewRank: Int,
    val bestRank: Int,
    val searchTarget: String,
    val subInfo: KotlinSubInfo? = null,
    val seriesInfo: KotlinSeriesInfo? = null
)