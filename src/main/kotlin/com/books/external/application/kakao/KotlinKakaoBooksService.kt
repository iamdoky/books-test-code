package com.books.external.application.kakao

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse

interface KotlinKakaoBooksService {

    fun search(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse
}