package com.books.external.application.aladin

import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import reactor.core.publisher.Mono

interface KotlinAladinBookService {
    
    suspend fun search(request: KotlinAladinBookRequest): KotlinAladinBookResponse
    
    // Reactor 호환성을 위한 메서드
    fun searchMono(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse>
}