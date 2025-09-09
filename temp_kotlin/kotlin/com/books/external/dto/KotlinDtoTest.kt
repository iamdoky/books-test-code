package com.books.external.dto

import com.books.external.api.payload.request.aladin.AladinBookRequest
import com.books.external.api.payload.response.aladin.AladinBookResponse
import com.books.external.api.payload.response.aladin.AladinSearchResponse
import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoDocument
import com.books.external.api.payload.kakao.response.KotlinKakaoMeta
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.request.naver.NaverSearchRequest
import com.books.external.api.payload.response.naver.NaverBookResponse
import com.books.external.api.payload.response.naver.NaverSearchResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class KotlinDtoTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `알라딘 Request DTO 직렬화 테스트`() {
        // Given
        val request = AladinBookRequest(
            "어린왕자",
            "Keyword",
            "10",
            "1",
            "Book",
            "PublishTime",
            "JS",
            "20131101"
        )

        // When
        val json = objectMapper.writeValueAsString(request)
        val deserializedRequest = objectMapper.readValue(json, AladinBookRequest::class.java)

        // Then
        assertEquals(request.query, deserializedRequest.query)
        assertEquals(request.queryType, deserializedRequest.queryType)
        assertEquals(request.maxResults, deserializedRequest.maxResults)
        assertEquals(request.start, deserializedRequest.start)
        assertEquals(request.searchTarget, deserializedRequest.searchTarget)
        assertEquals(request.sort, deserializedRequest.sort)
        assertEquals(request.output, deserializedRequest.output)
        assertEquals(request.version, deserializedRequest.version)
    }

    @Test
    fun `알라딘 Response DTO 역직렬화 테스트`() {
        // Given
        val jsonResponse = """
        {
            "version": "20131101",
            "logo": "https://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            "title": "알라딘 검색결과 - 어린왕자",
            "link": "https://www.aladin.co.kr/search/wsearchresult.aspx",
            "pubDate": "Mon, 01 Jan 2024 00:00:00 GMT",
            "totalResults": 1,
            "startIndex": 1,
            "query": "어린왕자",
            "searchCategoryId": 0,
            "searchCategoryName": "통합검색",
            "item": [
                {
                    "title": "어린왕자",
                    "link": "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123456",
                    "author": "생텍쥐페리",
                    "pubDate": "2024-01-01",
                    "description": "어린왕자에 대한 설명",
                    "isbn": "9788937460013",
                    "isbn13": "9788937460013",
                    "itemId": 123456,
                    "priceSales": 10000,
                    "priceStandard": 12000,
                    "mallType": "BOOK",
                    "stockStatus": "재고있음",
                    "mileage": 100,
                    "cover": "https://image.aladin.co.kr/product/123456/cover.jpg",
                    "categoryId": 1,
                    "categoryName": "문학",
                    "publisher": "민음사",
                    "salesPoint": 1000,
                    "adult": false,
                    "fixedPrice": true,
                    "customerReviewRank": 9.5,
                    "bestDuration": "",
                    "bestRank": 0,
                    "seriesInfo": null,
                    "subInfo": null
                }
            ]
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(jsonResponse, AladinBookResponse::class.java)

        // Then
        assertEquals("20131101", response.version)
        assertEquals("어린왕자", response.query)
        assertEquals(1, response.totalResults)
        assertEquals(1, response.item.size)
        assertEquals("어린왕자", response.item[0].title)
        assertEquals("생텍쥐페리", response.item[0].author)
        assertEquals("9788937460013", response.item[0].isbn)
    }

    @Test
    fun `카카오 Request DTO 검증 테스트`() {
        // Given
        val request = KotlinKakaoSearchRequest(
            query = "코틀린 인 액션",
            target = "title"
        )

        // When & Then
        assertEquals("코틀린 인 액션", request.query)
        assertEquals("title", request.target)
        
        // 유효성 검증
        assertTrue(request.query.isNotBlank())
        assertTrue(request.target.isNotEmpty())
    }

    @Test
    fun `카카오 Response DTO 역직렬화 테스트`() {
        // Given
        val jsonResponse = """
        {
            "documents": [
                {
                    "title": "코틀린 인 액션",
                    "contents": "코틀린에 대한 상세한 설명",
                    "url": "https://search.daum.net/search?q=코틀린",
                    "isbn": "9791161750712",
                    "datetime": "2024-01-01T00:00:00.000+09:00",
                    "authors": ["드미트리 제메로프", "스베트라나 이사코바"],
                    "publisher": "에이콘출판사",
                    "translators": ["오현석"],
                    "price": 36000,
                    "sale_price": 32400,
                    "thumbnail": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1640407%3Ftimestamp%3D20170331003432",
                    "status": "정상판매"
                }
            ],
            "meta": {
                "total_count": 1,
                "pageable_count": 1,
                "is_end": true
            }
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(jsonResponse, KotlinKakaoSearchResponse::class.java)

        // Then
        assertEquals(1, response.documents.size)
        assertEquals("코틀린 인 액션", response.documents[0].title)
        assertEquals(2, response.documents[0].authors.size)
        assertEquals("드미트리 제메로프", response.documents[0].authors[0])
        assertEquals("에이콘출판사", response.documents[0].publisher)
        assertEquals(1, response.meta.total_count)
        assertTrue(response.meta.is_end)
    }

    @Test
    fun `네이버 Request DTO 기본값 테스트`() {
        // Given & When
        val requestWithDefaults = NaverSearchRequest(
            "스프링 부트",
            10,
            1,
            "sim"
        )

        val requestCustom = NaverSearchRequest(
            "스프링 부트",
            20,
            5,
            "date"
        )

        // Then
        assertEquals("스프링 부트", requestWithDefaults.query)
        assertEquals(10, requestWithDefaults.display)
        assertEquals(1, requestWithDefaults.start)
        assertEquals("sim", requestWithDefaults.sort)

        assertEquals(20, requestCustom.display)
        assertEquals(5, requestCustom.start)
        assertEquals("date", requestCustom.sort)
    }

    @Test
    fun `네이버 Response DTO 역직렬화 테스트`() {
        // Given
        val jsonResponse = """
        {
            "lastBuildDate": "Mon, 01 Jan 2024 00:00:00 +0900",
            "total": 1,
            "start": 1,
            "display": 10,
            "items": [
                {
                    "title": "스프링 부트 실전 활용 마스터",
                    "link": "https://book.naver.com/bookdb/book_detail.nhn?bid=123456",
                    "image": "https://bookthumb-phinf.pstatic.net/cover/123/456/12345678.jpg",
                    "author": "그렉 턴키스트",
                    "discount": "27000",
                    "publisher": "한빛미디어",
                    "pubdate": "20240101",
                    "isbn": "9791169210034",
                    "description": "스프링 부트에 대한 실전 가이드"
                }
            ]
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(jsonResponse, NaverBookResponse::class.java)

        // Then
        assertEquals(1, response.total)
        assertEquals(1, response.start)
        assertEquals(10, response.display)
        assertEquals(1, response.items.size)
        assertEquals("스프링 부트 실전 활용 마스터", response.items[0].title)
        assertEquals("그렉 턴키스트", response.items[0].author)
        assertEquals("한빛미디어", response.items[0].publisher)
        assertEquals("9791169210034", response.items[0].isbn)
    }

    @Test
    fun `DTO 불변성 테스트`() {
        // Given - Java Record는 불변 객체
        val aladinRequest = AladinBookRequest(
            "테스트",
            "Keyword",
            "10",
            "1",
            "Book",
            "PublishTime",
            "JS",
            "20131101"
        )

        // Given - Kotlin Data Class는 val로 불변성 보장
        val kakaoRequest = KotlinKakaoSearchRequest(
            query = "테스트",
            target = "title"
        )

        // When & Then - Record는 불변 객체이므로 setter가 없음
        // 컴파일 시점에서 확인됨
        assertEquals("테스트", aladinRequest.query)
        assertEquals("테스트", kakaoRequest.query)

        // copy 메소드로만 변경 가능
        val modifiedKakaoRequest = kakaoRequest.copy(query = "수정된 테스트")
        assertEquals("수정된 테스트", modifiedKakaoRequest.query)
        assertEquals("테스트", kakaoRequest.query) // 원본은 변경되지 않음
    }

    @Test
    fun `빈 컬렉션 처리 테스트`() {
        // Given
        val emptyAladinResponse = AladinBookResponse(
            "20131101",
            "",
            "검색결과 없음",
            "",
            "",
            0,
            1,
            "존재하지않는책",
            0,
            "통합검색",
            emptyList()
        )

        val emptyKakaoResponse = KotlinKakaoSearchResponse(
            documents = emptyList(),
            meta = KotlinKakaoMeta(
                is_end = true,
                pageable_count = 0,
                total_count = 0
            )
        )

        // When & Then
        assertTrue(emptyAladinResponse.item.isEmpty())
        assertEquals(0, emptyAladinResponse.totalResults)
        
        assertTrue(emptyKakaoResponse.documents.isEmpty())
        assertEquals(0, emptyKakaoResponse.meta.total_count)
        assertTrue(emptyKakaoResponse.meta.is_end)
    }
}