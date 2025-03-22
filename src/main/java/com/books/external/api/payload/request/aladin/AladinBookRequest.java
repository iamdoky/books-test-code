package com.books.external.api.payload.request.aladin;


import io.swagger.v3.oas.annotations.media.Schema;

public record AladinBookRequest(

        @Schema(name = "query", description = "검색어", defaultValue = "어린왕자")
        String query,

        @Schema(name = "queryType", description = """
                검색어 종류,
                Keyword (기본값) : 제목+저자
                Title : 제목검색
                Author : 저자검색
                Publisher : 출판사검색
                """, defaultValue = "Keyword")
        String queryType,

        @Schema(name = "maxResults", description = "검색결과 한 페이지당 최대 출력 개수", defaultValue = "10")
        String maxResults,

        @Schema(name = "start", description = "검색결과 시작페이지", defaultValue = "1")
        String start,

        @Schema(name = "searchTarget", description = """
                        검색 대상 Mall,
                        Book(기본값) : 도서
                        Foreign : 외국도서
                        Music : 음반
                        DVD : DVD
                        Used : 중고샵(도서/음반/DVD 등)
                        eBook: 전자책
                        All : 위의 모든 타겟(몰)
                        """, defaultValue = "Book")
        String searchTarget,

        @Schema(name = "sort", description = """
                정렬 순서,
                Accuracy(기본값): 관련도
                PublishTime : 출간일
                Title : 제목
                SalesPoint : 판매량
                CustomerRating : 고객평점
                MyReviewCount :마이리뷰갯수
                """, defaultValue = "PublishTime")
        String sort,

        @Schema(name = "output", description = "XML / JS", defaultValue = "JS")
        String output,

        @Schema(name = "version", description = "검색API의 Version(날짜형식)을 설정, (최신버젼: 20131101)", defaultValue = "20131101")
        String version) {
}
