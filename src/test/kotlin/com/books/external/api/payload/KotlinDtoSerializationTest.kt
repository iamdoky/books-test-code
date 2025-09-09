package com.books.external.api.payload

import com.books.external.api.payload.request.aladin.AladinBookRequest
import com.books.external.api.payload.request.kakao.KakaoSearchRequest
import com.books.external.api.payload.request.naver.NaverSearchRequest
import com.books.external.api.payload.response.aladin.AladinBookResponse
import com.books.external.api.payload.response.aladin.AladinSearchResponse
import com.books.external.api.payload.response.kakao.KakaoBookResponse
import com.books.external.api.payload.response.kakao.KakaoDocument
import com.books.external.api.payload.response.kakao.KakaoMeta
import com.books.external.api.payload.response.naver.NaverBookResponse
import com.books.external.api.payload.response.naver.NaverSearchResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * DTO 직렬화/역직렬화 테스트 스위트
 * 목표: Java DTO들을 Kotlin data class로 변환하기 위한 TDD 테스트
 */
class KotlinDtoSerializationTest {

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = jacksonObjectMapper()
    }

    // ===== Request DTO 테스트 =====

    @Test
    @DisplayName("AladinBookRequest 직렬화/역직렬화 테스트")
    fun `should serialize and deserialize AladinBookRequest correctly`() {
        // Given
        val originalRequest = AladinBookRequest(
            query = "어린왕자",
            queryType = "Keyword",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "PublishTime",
            output = "JS",
            version = "20131101"
        )

        // When
        val json = objectMapper.writeValueAsString(originalRequest)
        val deserializedRequest = objectMapper.readValue(json, AladinBookRequest::class.java)

        // Then
        assertNotNull(deserializedRequest)
        assertEquals(originalRequest.query, deserializedRequest.query)
        assertEquals(originalRequest.queryType, deserializedRequest.queryType)
        assertEquals(originalRequest.maxResults, deserializedRequest.maxResults)
        assertEquals(originalRequest.start, deserializedRequest.start)
        assertEquals(originalRequest.searchTarget, deserializedRequest.searchTarget)
        assertEquals(originalRequest.sort, deserializedRequest.sort)
        assertEquals(originalRequest.output, deserializedRequest.output)
        assertEquals(originalRequest.version, deserializedRequest.version)
        
        // JSON 구조 검증
        assertTrue(json.contains("\"query\":\"어린왕자\""))
        assertTrue(json.contains("\"queryType\":\"Keyword\""))
    }

    @Test
    @DisplayName("KakaoSearchRequest 직렬화/역직렬화 테스트")
    fun `should serialize and deserialize KakaoSearchRequest correctly`() {
        // Given
        val originalRequest = KakaoSearchRequest("코틀린 인 액션")

        // When
        val json = objectMapper.writeValueAsString(originalRequest)
        val deserializedRequest = objectMapper.readValue(json, KakaoSearchRequest::class.java)

        // Then
        assertNotNull(deserializedRequest)
        assertEquals(originalRequest.query, deserializedRequest.query)
        assertTrue(json.contains("\"query\":\"코틀린 인 액션\""))
    }

    @Test
    @DisplayName("NaverSearchRequest 직렬화/역직렬화 테스트")
    fun `should serialize and deserialize NaverSearchRequest correctly`() {
        // Given
        val originalRequest = NaverSearchRequest(
            query = "스프링 부트",
            display = 15,
            start = 5,
            sort = "date"
        )

        // When
        val json = objectMapper.writeValueAsString(originalRequest)
        val deserializedRequest = objectMapper.readValue(json, NaverSearchRequest::class.java)

        // Then
        assertNotNull(deserializedRequest)
        assertEquals(originalRequest.query, deserializedRequest.query)
        assertEquals(originalRequest.display, deserializedRequest.display)
        assertEquals(originalRequest.start, deserializedRequest.start)
        assertEquals(originalRequest.sort, deserializedRequest.sort)
        
        assertTrue(json.contains("\"query\":\"스프링 부트\""))
        assertTrue(json.contains("\"display\":15"))
    }

    // ===== Response DTO 테스트 =====

    @Test
    @DisplayName("AladinBookResponse 역직렬화 테스트")
    fun `should deserialize AladinBookResponse from JSON correctly`() {
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
        assertNotNull(response)
        assertEquals("20131101", response.version)
        assertEquals("어린왕자", response.query)
        assertEquals(1, response.totalResults)
        assertEquals(1, response.item.size)
        
        val book = response.item[0]
        assertEquals("어린왕자", book.title)
        assertEquals("생텍쥐페리", book.author)
        assertEquals("9788937460013", book.isbn)
        assertEquals(123456, book.itemId)
        assertEquals(10000, book.priceSales)
    }

    @Test
    @DisplayName("KakaoBookResponse 역직렬화 테스트")
    fun `should deserialize KakaoBookResponse from JSON correctly`() {
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
                    "thumbnail": "https://search1.kakaocdn.net/thumb/cover.jpg",
                    "status": "정상판매"
                }
            ],
            "meta": {
                "total_count": 100,
                "pageable_count": 50,
                "is_end": false
            }
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(jsonResponse, KakaoBookResponse::class.java)

        // Then
        assertNotNull(response)
        assertEquals(100, response.meta.totalCount)
        assertEquals(50, response.meta.pageableCount)
        assertEquals(false, response.meta.isEnd)
        assertEquals(1, response.documents.size)
        
        val document = response.documents[0]
        assertEquals("코틀린 인 액션", document.title)
        assertEquals("에이콘출판사", document.publisher)
        assertEquals(2, document.authors.size)
        assertEquals("드미트리 제메로프", document.authors[0])
        assertEquals(36000, document.price)
    }

    @Test
    @DisplayName("NaverBookResponse 역직렬화 테스트")
    fun `should deserialize NaverBookResponse from JSON correctly`() {
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
        assertNotNull(response)
        assertEquals(1, response.total)
        assertEquals(1, response.start)
        assertEquals(10, response.display)
        assertEquals(1, response.items.size)
        
        val item = response.items[0]
        assertEquals("스프링 부트 실전 활용 마스터", item.title)
        assertEquals("그렉 턴키스트", item.author)
        assertEquals("한빛미디어", item.publisher)
        assertEquals("9791169210034", item.isbn)
    }

    // ===== 에러 처리 테스트 =====

    @Test
    @DisplayName("잘못된 JSON 형식 처리 테스트")
    fun `should handle invalid JSON gracefully`() {
        // Given
        val invalidJson = "{ invalid json }"

        // When & Then
        assertThrows<Exception> {
            objectMapper.readValue(invalidJson, AladinBookResponse::class.java)
        }
    }

    @Test
    @DisplayName("누락된 필수 필드 처리 테스트")
    fun `should handle missing required fields`() {
        // Given
        val incompleteJson = """
        {
            "version": "20131101"
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(incompleteJson, AladinBookResponse::class.java)

        // Then
        assertNotNull(response)
        assertEquals("20131101", response.version)
        // 다른 필드들은 기본값 또는 null
    }

    @Test
    @DisplayName("빈 배열 처리 테스트")
    fun `should handle empty arrays in responses`() {
        // Given
        val emptyArrayJson = """
        {
            "version": "20131101",
            "logo": "",
            "title": "검색결과 없음",
            "link": "",
            "pubDate": "",
            "totalResults": 0,
            "startIndex": 1,
            "query": "존재하지않는책",
            "searchCategoryId": 0,
            "searchCategoryName": "통합검색",
            "item": []
        }
        """.trimIndent()

        // When
        val response = objectMapper.readValue(emptyArrayJson, AladinBookResponse::class.java)

        // Then
        assertNotNull(response)
        assertEquals(0, response.totalResults)
        assertTrue(response.item.isEmpty())
    }

    @Test
    @DisplayName("특수 문자 처리 테스트")
    fun `should handle special characters in JSON`() {
        // Given
        val specialCharJson = """
        {
            "query": "어린왕자 & 장미: \"사랑\"의 의미 (완전판)"
        }
        """.trimIndent()

        // When
        val request = objectMapper.readValue(specialCharJson, KakaoSearchRequest::class.java)

        // Then
        assertNotNull(request)
        assertEquals("어린왕자 & 장미: \"사랑\"의 의미 (완전판)", request.query)
    }

    @Test
    @DisplayName("대용량 데이터 처리 성능 테스트")
    fun `should handle large data sets efficiently`() {
        // Given
        val largeItemList = (1..100).map { index ->
            mapOf(
                "title" to "Book $index",
                "author" to "Author $index",
                "isbn" to "978000000000$index",
                "itemId" to index,
                "priceSales" to (10000 + index * 100),
                "priceStandard" to (12000 + index * 100),
                "publisher" to "Publisher $index"
            )
        }

        val largeResponseMap = mapOf(
            "version" to "20131101",
            "logo" to "",
            "title" to "대용량 검색결과",
            "link" to "",
            "pubDate" to "",
            "totalResults" to 100,
            "startIndex" to 1,
            "query" to "테스트",
            "searchCategoryId" to 0,
            "searchCategoryName" to "통합검색",
            "item" to largeItemList
        )

        val largeJson = objectMapper.writeValueAsString(largeResponseMap)

        // When
        val startTime = System.currentTimeMillis()
        val response = objectMapper.readValue(largeJson, AladinBookResponse::class.java)
        val endTime = System.currentTimeMillis()

        // Then
        assertNotNull(response)
        assertEquals(100, response.totalResults)
        assertEquals(100, response.item.size)
        assertTrue(endTime - startTime < 1000) // 1초 이내 처리
    }
}