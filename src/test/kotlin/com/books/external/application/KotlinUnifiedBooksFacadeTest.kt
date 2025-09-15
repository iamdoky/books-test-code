package com.books.external.application

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.kakao.response.KotlinKakaoMeta
import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import com.books.external.application.aladin.KotlinAladinBookService
import com.books.external.application.kakao.KotlinKakaoBooksService
import com.books.external.application.naver.KotlinNaverBookService
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class KotlinUnifiedBooksFacadeTest {

    @Mock
    private lateinit var aladinBookService: KotlinAladinBookService

    @Mock
    private lateinit var kakaoBookService: KotlinKakaoBooksService

    @Mock
    private lateinit var naverBookService: KotlinNaverBookService

    private lateinit var kotlinUnifiedBooksFacade: KotlinUnifiedBooksFacade

    @BeforeEach
    fun setUp() {
        kotlinUnifiedBooksFacade = KotlinUnifiedBooksFacade(
            aladinBookService,
            kakaoBookService,
            naverBookService
        )
    }

    @Test
    fun `searchAladin_알라딘_서비스_정상_호출`() = runBlocking {
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

        val expectedResponse = KotlinAladinBookResponse(
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

        `when`(aladinBookService.search(any<KotlinAladinBookRequest>()))
            .thenReturn(expectedResponse)

        // When
        val result = kotlinUnifiedBooksFacade.searchAladin(request)

        // Then
        assertThat(result).isEqualTo(expectedResponse)
    }

    @Test
    fun `searchKakao_카카오_서비스_정상_호출`() = runBlocking {
        // Given
        val request = KotlinKakaoSearchRequest("Spring Boot", "title")

        val expectedResponse = KotlinKakaoSearchResponse(
            meta = KotlinKakaoMeta(
                total_count = 100,
                pageable_count = 50,
                is_end = false
            ),
            documents = emptyList()
        )

        `when`(kakaoBookService.search(any<KotlinKakaoSearchRequest>()))
            .thenReturn(expectedResponse)

        // When
        val result = kotlinUnifiedBooksFacade.searchKakao(request)

        // Then
        assertThat(result).isEqualTo(expectedResponse)
    }

    @Test
    fun `searchNaver_네이버_서비스_정상_호출`() = runBlocking {
        // Given
        val request = KotlinNaverSearchRequest("Spring Boot", 10, 1)

        val expectedResponse = KotlinNaverBookResponse(
            lastBuildDate = "Wed, 06 Nov 2024 17:34:14 +0900",
            total = 100,
            start = 1,
            display = 10,
            items = emptyList()
        )

        `when`(naverBookService.search(any<KotlinNaverSearchRequest>()))
            .thenReturn(expectedResponse)

        // When
        val result = kotlinUnifiedBooksFacade.searchNaver(request)

        // Then
        assertThat(result).isEqualTo(expectedResponse)
    }

    @Test
    fun `searchAladinMono_Mono_형태로_알라딘_서비스_호출`() {
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

        val expectedResponse = KotlinAladinBookResponse(
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

        `when`(aladinBookService.searchMono(any<KotlinAladinBookRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = kotlinUnifiedBooksFacade.searchAladinMono(request)

        // Then
        StepVerifier.create(result)
            .expectNext(expectedResponse)
            .verifyComplete()
    }

    @Test
    fun `searchAll_모든_API_병렬_호출_성공`() = runBlocking {
        // Given
        val keyword = "Spring Boot"

        val aladinResponse = KotlinAladinBookResponse(
            version = "20131101",
            logo = "http://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - Spring Boot",
            link = "http://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Wed, 06 Nov 2024 17:34:14 GMT",
            totalResults = 145,
            startIndex = 1,
            query = keyword,
            searchCategoryId = 0,
            searchCategoryName = "전체",
            item = emptyList()
        )

        val kakaoResponse = KotlinKakaoSearchResponse(
            meta = KotlinKakaoMeta(
                total_count = 100,
                pageable_count = 50,
                is_end = false
            ),
            documents = emptyList()
        )

        val naverResponse = KotlinNaverBookResponse(
            lastBuildDate = "Wed, 06 Nov 2024 17:34:14 +0900",
            total = 100,
            start = 1,
            display = 10,
            items = emptyList()
        )

        `when`(aladinBookService.search(any<KotlinAladinBookRequest>()))
            .thenReturn(aladinResponse)
        `when`(kakaoBookService.search(any<KotlinKakaoSearchRequest>()))
            .thenReturn(kakaoResponse)
        `when`(naverBookService.search(any<KotlinNaverSearchRequest>()))
            .thenReturn(naverResponse)

        // When
        val result = kotlinUnifiedBooksFacade.searchAll(keyword)

        // Then
        assertThat(result.keyword).isEqualTo(keyword)
        assertThat(result.aladinResult).isEqualTo(aladinResponse)
        assertThat(result.kakaoResult).isEqualTo(kakaoResponse)
        assertThat(result.naverResult).isEqualTo(naverResponse)
        assertThat(result.hasAnyResults()).isTrue()
        assertThat(result.getSuccessfulApiCount()).isEqualTo(3)
    }

    @Test
    fun `getSearchStatistics_검색_통계_정상_반환`() = runBlocking {
        // Given
        val keyword = "Spring Boot"

        val aladinResponse = KotlinAladinBookResponse(
            version = "20131101",
            logo = "http://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - Spring Boot",
            link = "http://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Wed, 06 Nov 2024 17:34:14 GMT",
            totalResults = 145,
            startIndex = 1,
            query = keyword,
            searchCategoryId = 0,
            searchCategoryName = "전체",
            item = emptyList()
        )

        val kakaoResponse = KotlinKakaoSearchResponse(
            meta = KotlinKakaoMeta(
                total_count = 100,
                pageable_count = 50,
                is_end = false
            ),
            documents = emptyList()
        )

        `when`(aladinBookService.search(any<KotlinAladinBookRequest>()))
            .thenReturn(aladinResponse)
        `when`(kakaoBookService.search(any<KotlinKakaoSearchRequest>()))
            .thenReturn(kakaoResponse)
        `when`(naverBookService.search(any<KotlinNaverSearchRequest>()))
            .thenThrow(RuntimeException("Naver API Error"))

        // When
        val result = kotlinUnifiedBooksFacade.getSearchStatistics(keyword)

        // Then
        assertThat(result.searchKeyword).isEqualTo(keyword)
        assertThat(result.successfulApis).isEqualTo(2)
        assertThat(result.failedApis).isEqualTo(1)
        assertThat(result.successRate).isEqualTo(66.66666666666667)
    }
}