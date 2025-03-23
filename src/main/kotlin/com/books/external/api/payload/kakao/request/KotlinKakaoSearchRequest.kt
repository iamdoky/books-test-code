package com.books.external.api.payload.kakao.request

import io.swagger.v3.oas.annotations.media.Schema

data class KotlinKakaoSearchRequest(

    @Schema(
        name = "query",
        description = "검색을 원하는 질의어",
        defaultValue = "클린코드"
    )
    val query: String,

    @Schema(
        name = "target",
        description = """
            검색 필드 제한, 사용 가능한 값: title(제목), isbn (ISBN), publisher(출판사), person(인명)
        """,
        defaultValue = "title"
    )
    val target: String
)