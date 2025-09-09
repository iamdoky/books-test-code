package com.books.external.api

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
import com.books.external.application.ExternalBooksFacade
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringJUnitExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * ExternalController의 Kotlin 테스트 스위트
 * 목표: Java ExternalController를 Kotlin으로 변환하기 위한 TDD 테스트
 */
@ExtendWith(SpringJUnitExtension::class)
@WebMvcTest(ExternalController::class)
class KotlinExternalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var externalBooksFacade: ExternalBooksFacade

    private lateinit var aladinRequest: AladinBookRequest
    private lateinit var kakaoRequest: KakaoSearchRequest
    private lateinit var naverRequest: NaverSearchRequest

    @BeforeEach
    fun setUp() {
        aladinRequest = AladinBookRequest(
            query = "어린왕자",
            queryType = "Keyword",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "PublishTime",
            output = "JS",
            version = "20131101"
        )

        kakaoRequest = KakaoSearchRequest("코틀린 인 액션")

        naverRequest = NaverSearchRequest(
            query = "스프링 부트",
            display = 10,
            start = 1,
            sort = "sim"
        )
    }

    @Test
    @DisplayName("알라딘 도서 검색 API 호출 성공 테스트")
    fun `should successfully search books via Aladin API`() {
        // Given
        val expectedResponse = createMockAladinResponse()
        whenever(externalBooksFacade.search(any<AladinBookRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When & Then
        mockMvc.perform(
            post("/api/external/aladin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aladinRequest))
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
    }

    @Test
    @DisplayName("카카오 도서 검색 API 호출 성공 테스트")
    fun `should successfully search books via Kakao API`() {
        // Given
        val expectedResponse = createMockKakaoResponse()
        whenever(externalBooksFacade.search(any<KakaoSearchRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When & Then
        mockMvc.perform(
            post("/api/external/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kakaoRequest))
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
    }

    @Test
    @DisplayName("네이버 도서 검색 API 호출 성공 테스트")
    fun `should successfully search books via Naver API`() {
        // Given
        val expectedResponse = createMockNaverResponse()
        whenever(externalBooksFacade.search(any<NaverSearchRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When & Then
        mockMvc.perform(
            post("/api/external/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(naverRequest))
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
    }

    @Test
    @DisplayName("알라딘 API 잘못된 요청 데이터 검증 테스트")
    fun `should return bad request for invalid Aladin request`() {
        // Given
        val invalidRequest = AladinBookRequest(
            query = "", // 빈 쿼리
            queryType = "Keyword",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "PublishTime",
            output = "JS",
            version = "20131101"
        )

        // When & Then
        mockMvc.perform(
            post("/api/external/aladin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("ExternalController가 Reactive 응답을 올바르게 처리하는지 테스트")
    fun `should handle reactive responses correctly`() {
        // Given
        val expectedResponse = createMockAladinResponse()
        whenever(externalBooksFacade.search(any<AladinBookRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When & Then
        mockMvc.perform(
            post("/api/external/aladin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aladinRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.query").value("어린왕자"))
    }

    // Mock 데이터 생성 헬퍼 메서드들
    private fun createMockAladinResponse(): AladinBookResponse {
        return AladinBookResponse(
            version = "20131101",
            logo = "https://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - 어린왕자",
            link = "https://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Mon, 01 Jan 2024 00:00:00 GMT",
            totalResults = 1,
            startIndex = 1,
            query = "어린왕자",
            searchCategoryId = 0,
            searchCategoryName = "통합검색",
            item = listOf(
                AladinSearchResponse(
                    title = "어린왕자",
                    link = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123456",
                    author = "생텍쥐페리",
                    pubDate = "2024-01-01",
                    description = "어린왕자에 대한 설명",
                    isbn = "9788937460013",
                    isbn13 = "9788937460013",
                    itemId = 123456,
                    priceSales = 10000,
                    priceStandard = 12000,
                    mallType = "BOOK",
                    stockStatus = "재고있음",
                    mileage = 100,
                    cover = "https://image.aladin.co.kr/product/123456/cover.jpg",
                    categoryId = 1,
                    categoryName = "문학",
                    publisher = "민음사",
                    salesPoint = 1000,
                    adult = false,
                    fixedPrice = true,
                    customerReviewRank = 9.5,
                    bestDuration = "",
                    bestRank = 0,
                    seriesInfo = null,
                    subInfo = null
                )
            )
        )
    }

    private fun createMockKakaoResponse(): KakaoBookResponse {
        return KakaoBookResponse(
            meta = KakaoMeta(
                totalCount = 100,
                pageableCount = 50,
                isEnd = false
            ),
            documents = listOf(
                KakaoDocument(
                    title = "코틀린 인 액션",
                    contents = "코틀린에 대한 상세한 설명",
                    url = "https://search.daum.net/search?q=코틀린",
                    isbn = "9791161750712",
                    datetime = LocalDateTime.of(2024, 1, 1, 0, 0),
                    authors = listOf("드미트리 제메로프", "스베트라나 이사코바"),
                    publisher = "에이콘출판사",
                    translators = listOf("오현석"),
                    price = 36000,
                    salePrice = 32400,
                    thumbnail = "https://search1.kakaocdn.net/thumb/cover.jpg",
                    status = "정상판매"
                )
            )
        )
    }

    private fun createMockNaverResponse(): NaverBookResponse {
        return NaverBookResponse(
            lastBuildDate = "Mon, 01 Jan 2024 00:00:00 +0900",
            total = 1,
            start = 1,
            display = 10,
            items = listOf(
                NaverSearchResponse(
                    title = "스프링 부트 실전 활용 마스터",
                    link = "https://book.naver.com/bookdb/book_detail.nhn?bid=123456",
                    image = "https://bookthumb-phinf.pstatic.net/cover/123/456/12345678.jpg",
                    author = "그렉 턴키스트",
                    discount = "27000",
                    publisher = "한빛미디어",
                    pubdate = "20240101",
                    isbn = "9791169210034",
                    description = "스프링 부트에 대한 실전 가이드"
                )
            )
        )
    }
}