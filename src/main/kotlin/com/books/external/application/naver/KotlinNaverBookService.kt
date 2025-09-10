package com.books.external.application.naver

import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import reactor.core.publisher.Mono

interface KotlinNaverBookService {
    
    suspend fun search(request: KotlinNaverSearchRequest): KotlinNaverBookResponse
    
    // Reactor 호환성을 위한 메서드
    fun searchMono(request: KotlinNaverSearchRequest): Mono<KotlinNaverBookResponse>
}