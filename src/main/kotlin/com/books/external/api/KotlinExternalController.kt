package com.books.external.api

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.application.KotlinBooksFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "[코틀린] 외부 도서 호출")
@RestController
@RequestMapping("/kotlin/api/external")
class KotlinExternalController(private val booksFacade: KotlinBooksFacade) {

    @PostMapping("/kakao")
    fun searchKakao(@RequestBody request: KotlinKakaoSearchRequest): ResponseEntity<KotlinKakaoSearchResponse> {

        return ResponseEntity.ok(booksFacade.search(request))
    }
}