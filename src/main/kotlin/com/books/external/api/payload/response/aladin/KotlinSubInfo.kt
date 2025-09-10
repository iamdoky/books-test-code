package com.books.external.api.payload.response.aladin

data class KotlinSubInfo(
    val subbarcode: String,
    val subTitle: String,
    val originalTitle: String,
    val cardReviewImgList: List<String>
)