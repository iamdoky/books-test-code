package com.books.external.application

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.application.kakao.KotlinKakaoBooksService
import org.springframework.stereotype.Service

@Service
class KotlinBooksFacade(private val kotlinKakaoBooksService: KotlinKakaoBooksService) {

    suspend fun search(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse {

        return kotlinKakaoBooksService.search(request)
    }
}