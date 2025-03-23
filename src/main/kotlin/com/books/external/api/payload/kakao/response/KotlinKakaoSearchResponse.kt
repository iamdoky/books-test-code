package com.books.external.api.payload.kakao.response


data class KotlinKakaoSearchResponse(

    val documents: List<KotlinKakaoDocument>,
    val meta: KotlinKakaoMeta
)