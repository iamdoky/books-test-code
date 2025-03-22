package com.books.external.api.payload.request.kakao;


import io.swagger.v3.oas.annotations.media.Schema;

public record KakaoSearchRequest(

        @Schema(name = "query", description = "검색을 원하는 질의어", defaultValue = "클린코드")
        String query,

        @Schema(name = "target",
                description = """
                        검색 필드 제한, 사용 가능한 값: title(제목), isbn (ISBN), publisher(출판사), person(인명)
                        """, defaultValue = "title")
        String target) {
}
