package com.books.external.api.payload.request.aladin

import io.swagger.v3.oas.annotations.media.Schema

data class KotlinAladinBookRequest(

    @field:Schema(name = "query", description = "검색어", example = "어린왕자")
    val query: String,

    @field:Schema(
        name = "queryType", 
        description = """
            검색어 종류,
            Keyword (기본값) : 제목+저자
            Title : 제목검색
            Author : 저자검색
            Publisher : 출판사검색
            """, 
        example = "Keyword"
    )
    val queryType: String = "Keyword",

    @field:Schema(name = "maxResults", description = "검색결과 한 페이지당 최대 출력 개수", example = "10")
    val maxResults: String = "10",

    @field:Schema(name = "start", description = "검색결과 시작페이지", example = "1")
    val start: String = "1",

    @field:Schema(
        name = "searchTarget",
        description = """
            검색 대상 Mall,
            Book(기본값) : 도서
            Foreign : 외국도서
            Music : 음반
            DVD : DVD
            Used : 중고샵(도서/음반/DVD 등)
            eBook: 전자책
            All : 위의 모든 타겟(몰)
            """,
        example = "Book"
    )
    val searchTarget: String = "Book",

    @field:Schema(
        name = "sort",
        description = """
            정렬 순서,
            Accuracy(기본값): 관련도
            PublishTime : 출간일
            Title : 제목
            SalesPoint : 판매량
            CustomerRating : 고객평점
            MyReviewCount :마이리뷰갯수
            """,
        example = "PublishTime"
    )
    val sort: String = "PublishTime",

    @field:Schema(name = "output", description = "XML / JS", example = "JS")
    val output: String = "JS",

    @field:Schema(name = "version", description = "검색API의 Version(날짜형식)을 설정, (최신버젼: 20131101)", example = "20131101")
    val version: String = "20131101"
)