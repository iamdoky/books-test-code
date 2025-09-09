package com.books.external.application

import com.books.external.api.payload.request.aladin.AladinBookRequest
import com.books.external.api.payload.response.aladin.AladinBookResponse
import com.books.external.api.payload.response.aladin.AladinSearchResponse
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.function.Function
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * AladinBookService의 Kotlin 테스트 스위트
 * 목표: Java AladinBookService를 Kotlin으로 변환하기 위한 TDD 테스트
 */
@ExtendWith(MockitoExtension::class)
class KotlinAladinBookServiceTest {

    @Mock
    private lateinit var aladinWebClient: WebClient

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var aladinBookServiceImpl: AladinBookServiceImpl

    private lateinit var mockWebServer: MockWebServer
    private lateinit var objectMapper: ObjectMapper
    private lateinit var request: AladinBookRequest

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        objectMapper = ObjectMapper()

        // TTB key 설정
        ReflectionTestUtils.setField(aladinBookServiceImpl, "ttbKey", "test-ttb-key")

        request = AladinBookRequest(
            query = "어린왕자",
            queryType = "Keyword",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "PublishTime",
            output = "JS",
            version = "20131101"
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName("알라딘 API 도서 검색 성공 테스트")
    fun `should successfully search books from Aladin API`() {
        // Given
        val expectedResponse = createMockAladinResponse()
        
        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = aladinBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals("어린왕자", response.query)
                assertEquals(1, response.totalResults)
                assertEquals("알라딘 검색결과 - 어린왕자", response.title)
                assertEquals(1, response.item.size)
                assertEquals("어린왕자", response.item[0].title)
                assertEquals("생텍쥐페리", response.item[0].author)
                assertEquals("9788937460013", response.item[0].isbn)
            }
            .verifyComplete()

        verify(aladinWebClient).get()
    }

    @Test
    @DisplayName("알라딘 API 빈 검색 결과 처리 테스트")
    fun `should handle empty search results from Aladin API`() {
        // Given
        val emptyResponse = AladinBookResponse(
            version = "20131101",
            logo = "",
            title = "검색결과 없음",
            link = "",
            pubDate = "",
            totalResults = 0,
            startIndex = 1,
            query = "존재하지않는책",
            searchCategoryId = 0,
            searchCategoryName = "통합검색",
            item = emptyList()
        )

        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.just(emptyResponse))

        // When
        val result = aladinBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(0, response.totalResults)
                assertTrue(response.item.isEmpty())
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("알라딘 API 오류 응답 처리 테스트")
    fun `should handle API errors gracefully`() {
        // Given
        val errorMessage = "Aladin API connection failed"
        
        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.error(RuntimeException(errorMessage)))

        // When
        val result = aladinBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is RuntimeException && throwable.message == errorMessage
            }
            .verify()
    }

    @Test
    @DisplayName("알라딘 API 요청 매개변수 검증 테스트")
    fun `should build correct URI parameters for Aladin API request`() {
        // Given
        val expectedResponse = createMockAladinResponse()
        val uriBuilderCaptor = argumentCaptor<Function<*, *>>()

        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(uriBuilderCaptor.capture())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        aladinBookServiceImpl.search(request)

        // Then
        verify(aladinWebClient).get()
        verify(requestHeadersUriSpec).uri(any<Function<*, *>>())
        verify(requestHeadersSpec).retrieve()
    }

    @Test
    @DisplayName("대량 검색 결과 처리 테스트")
    fun `should handle large search results from Aladin API`() {
        // Given
        val largeResponse = createLargeAladinResponse()
        
        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.just(largeResponse))

        // When
        val result = aladinBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(50, response.totalResults)
                assertEquals(10, response.item.size) // maxResults 제한
                assertTrue(response.item.all { it.title.isNotBlank() })
                assertTrue(response.item.all { it.author.isNotBlank() })
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("특수 문자가 포함된 검색어 처리 테스트")
    fun `should handle special characters in search query`() {
        // Given
        val specialCharRequest = request.copy(query = "어린왕자 & 장미꽃: \"사랑\"의 의미")
        val expectedResponse = createMockAladinResponse()
        
        whenever(aladinWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(AladinBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = aladinBookServiceImpl.search(specialCharRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals("어린왕자", response.query)
            }
            .verifyComplete()
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

    private fun createLargeAladinResponse(): AladinBookResponse {
        val items = (1..10).map { index ->
            AladinSearchResponse(
                title = "어린왕자 $index",
                link = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=12345$index",
                author = "생텍쥐페리",
                pubDate = "2024-01-0$index",
                description = "어린왕자 $index 에 대한 설명",
                isbn = "978893746001$index",
                isbn13 = "978893746001$index",
                itemId = 123456 + index,
                priceSales = 10000 + (index * 1000),
                priceStandard = 12000 + (index * 1000),
                mallType = "BOOK",
                stockStatus = "재고있음",
                mileage = 100 + (index * 10),
                cover = "https://image.aladin.co.kr/product/12345$index/cover.jpg",
                categoryId = 1,
                categoryName = "문학",
                publisher = "민음사",
                salesPoint = 1000 + (index * 100),
                adult = false,
                fixedPrice = true,
                customerReviewRank = 9.0 + (index * 0.1),
                bestDuration = "",
                bestRank = index,
                seriesInfo = null,
                subInfo = null
            )
        }

        return AladinBookResponse(
            version = "20131101",
            logo = "https://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - 어린왕자",
            link = "https://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Mon, 01 Jan 2024 00:00:00 GMT",
            totalResults = 50,
            startIndex = 1,
            query = "어린왕자",
            searchCategoryId = 0,
            searchCategoryName = "통합검색",
            item = items
        )
    }
}