package com.books.external.api.payload.request.naver

import io.swagger.v3.oas.annotations.media.Schema

data class KotlinNaverSearchRequest(

    @field:Schema(name = "keyword", description = "키워드 ex) 제목, ISBN, 저자명", example = "클린코드")
    val keyword: String,

    @field:Schema(name = "display", description = "한 번에 표시할 검색 결과 개수(기본값: 10, 최댓값: 100)", example = "10")
    val display: Int = 10,

    @field:Schema(name = "start", description = "검색 시작 위치(기본값: 1, 최댓값: 1000)", example = "1")
    val start: Int = 1
) {
    init {
        require(display in 1..100) { "display는 1-100 범위여야 합니다" }
        require(start in 1..1000) { "start는 1-1000 범위여야 합니다" }
        require(keyword.isNotBlank()) { "검색 키워드는 필수입니다" }
    }
}