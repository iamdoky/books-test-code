package com.books.external.api.payload.kakao.response

data class KotlinKakaoDocument(

    val authors: Array<String>,
    val contents: String,
    val datetime: String,
    val isbn: String,
    val price: Int,
    val publisher: String,
    val salePrice: Int,
    val status: String,
    val thumbnail: String,
    val title: String,
    val translators: Array<String>,
    val url: String
)
