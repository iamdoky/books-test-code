package com.books.external.api

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.kakao.response.KotlinKakaoMeta
import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import com.books.external.application.KotlinUnifiedBooksFacade
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import reactor.core.publisher.Mono

@WebMvcTest(KotlinExternalController::class)
class KotlinExternalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @org.springframework.boot.test.mock.mockito.MockBean
    private lateinit var kotlinUnifiedBooksFacade: KotlinUnifiedBooksFacade

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `searchAladin_알라딘_API_성공적으로_호출`() {
        // Given
        val request = KotlinAladinBookRequest(
            query = "Spring Boot",
            queryType = "Title",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "SalesPoint",
            output = "js",
            version = "20131101"
        )

        val response = KotlinAladinBookResponse(
            version = "20131101",
            logo = "http://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - Spring Boot",
            link = "http://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Wed, 06 Nov 2024 17:34:14 GMT",
            totalResults = 145,
            startIndex = 1,
            query = "Spring Boot",
            searchCategoryId = 0,
            searchCategoryName = "전체",
            item = emptyList()
        )

        `when`(kotlinUnifiedBooksFacade.searchAladinMono(any(KotlinAladinBookRequest::class.java)))
            .thenReturn(Mono.just(response))

        // When & Then
        mockMvc.perform(
            post("/api/external/kotlin/aladin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `searchKakao_카카오_API_성공적으로_호출`() {
        // Given
        val request = KotlinKakaoSearchRequest("Spring Boot", "title")

        val response = KotlinKakaoSearchResponse(
            meta = KotlinKakaoMeta(
                total_count = 100,
                pageable_count = 50,
                is_end = false
            ),
            documents = emptyList()
        )

        `when`(kotlinUnifiedBooksFacade.searchKakaoMono(any(KotlinKakaoSearchRequest::class.java)))
            .thenReturn(Mono.just(response))

        // When & Then
        mockMvc.perform(
            post("/api/external/kotlin/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `searchNaver_네이버_API_성공적으로_호출`() {
        // Given
        val request = KotlinNaverSearchRequest("Spring Boot", 10, 1)

        val response = KotlinNaverBookResponse(
            lastBuildDate = "Wed, 06 Nov 2024 17:34:14 +0900",
            total = 100,
            start = 1,
            display = 10,
            items = emptyList()
        )

        `when`(kotlinUnifiedBooksFacade.searchNaverMono(any(KotlinNaverSearchRequest::class.java)))
            .thenReturn(Mono.just(response))

        // When & Then
        mockMvc.perform(
            post("/api/external/kotlin/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }
}