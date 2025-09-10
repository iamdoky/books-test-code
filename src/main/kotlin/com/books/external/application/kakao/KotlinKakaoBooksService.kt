package com.books.external.application.kakao

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import reactor.core.publisher.Mono

interface KotlinKakaoBooksService {

    suspend fun search(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse
    
    // Reactor 호환성을 위한 메서드
    fun searchMono(request: KotlinKakaoSearchRequest): Mono<KotlinKakaoSearchResponse>
}