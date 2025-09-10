package com.books.external.api

import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import com.books.external.application.KotlinUnifiedBooksFacade
import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@Tag(name = "Kotlin 외부 도서 호출")
@RestController
@RequestMapping("/api/external/kotlin")
class KotlinExternalController(
    private val kotlinUnifiedBooksFacade: KotlinUnifiedBooksFacade
) {

    @PostMapping("/aladin")
    fun searchAladin(
        @RequestBody request: KotlinAladinBookRequest
    ): ResponseEntity<Mono<KotlinAladinBookResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchAladinMono(request))
    }

    @PostMapping("/kakao")  
    fun searchKakao(
        @RequestBody request: KotlinKakaoSearchRequest
    ): ResponseEntity<Mono<KotlinKakaoSearchResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchKakaoMono(request))
    }

    @PostMapping("/naver")
    fun searchNaver(
        @RequestBody request: KotlinNaverSearchRequest
    ): ResponseEntity<Mono<KotlinNaverBookResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchNaverMono(request))
    }
}