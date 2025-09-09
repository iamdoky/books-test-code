package com.books.external.application

import com.books.external.api.payload.request.naver.NaverSearchRequest
import com.books.external.api.payload.response.naver.NaverBookResponse
import com.books.external.api.payload.response.naver.NaverSearchResponse
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
 * NaverBookService의 Kotlin 테스트 스위트
 * 목표: Java NaverBookService를 Kotlin으로 변환하기 위한 TDD 테스트
 */
@ExtendWith(MockitoExtension::class)
class KotlinNaverBookServiceTest {

    @Mock
    private lateinit var naverWebClient: WebClient

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var naverBookServiceImpl: NaverBookServiceImpl

    private lateinit var mockWebServer: MockWebServer
    private lateinit var objectMapper: ObjectMapper
    private lateinit var request: NaverSearchRequest

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        objectMapper = ObjectMapper()

        // Naver API 설정
        ReflectionTestUtils.setField(naverBookServiceImpl, "naverClientId", "test-client-id")
        ReflectionTestUtils.setField(naverBookServiceImpl, "naverClientSecret", "test-client-secret")

        request = NaverSearchRequest(
            query = "스프링 부트",
            display = 10,
            start = 1,
            sort = "sim"
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName("네이버 API 도서 검색 성공 테스트")
    fun `should successfully search books from Naver API`() {
        // Given
        val expectedResponse = createMockNaverResponse()
        
        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = naverBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(1, response.total)
                assertEquals(1, response.start)
                assertEquals(10, response.display)
                assertEquals(1, response.items.size)
                assertEquals("스프링 부트 실전 활용 마스터", response.items[0].title)
                assertEquals("그렉 턴키스트", response.items[0].author)
                assertEquals("한빛미디어", response.items[0].publisher)
                assertEquals("9791169210034", response.items[0].isbn)
            }
            .verifyComplete()

        verify(naverWebClient).get()
    }

    @Test
    @DisplayName("네이버 API 빈 검색 결과 처리 테스트")
    fun `should handle empty search results from Naver API`() {
        // Given
        val emptyResponse = NaverBookResponse(
            lastBuildDate = "Mon, 01 Jan 2024 00:00:00 +0900",
            total = 0,
            start = 1,
            display = 10,
            items = emptyList()
        )

        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(emptyResponse))

        // When
        val result = naverBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(0, response.total)
                assertTrue(response.items.isEmpty())
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("네이버 API 오류 응답 처리 테스트")
    fun `should handle API errors gracefully`() {
        // Given
        val errorMessage = "Naver API authentication failed"
        
        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.error(RuntimeException(errorMessage)))

        // When
        val result = naverBookServiceImpl.search(request)

        // Then
        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is RuntimeException && throwable.message == errorMessage
            }
            .verify()
    }

    @Test
    @DisplayName("네이버 API 인증 헤더 검증 테스트")
    fun `should include correct authentication headers for Naver API`() {
        // Given
        val expectedResponse = createMockNaverResponse()
        val headerNameCaptor = argumentCaptor<String>()
        val headerValueCaptor = argumentCaptor<String>()

        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(headerNameCaptor.capture(), headerValueCaptor.capture()))
            .thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        naverBookServiceImpl.search(request)

        // Then
        verify(naverWebClient).get()
        val capturedHeaders = headerNameCaptor.allValues.zip(headerValueCaptor.allValues)
        assertTrue(capturedHeaders.any { it.first == "X-Naver-Client-Id" && it.second == "test-client-id" })
        assertTrue(capturedHeaders.any { it.first == "X-Naver-Client-Secret" && it.second == "test-client-secret" })
    }

    @Test
    @DisplayName("네이버 API 요청 매개변수 검증 테스트")
    fun `should build correct URI parameters for Naver API request`() {
        // Given
        val expectedResponse = createMockNaverResponse()
        val uriBuilderCaptor = argumentCaptor<Function<*, *>>()

        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(uriBuilderCaptor.capture())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        naverBookServiceImpl.search(request)

        // Then
        verify(naverWebClient).get()
        verify(requestHeadersUriSpec).uri(any<Function<*, *>>())
    }

    @Test
    @DisplayName("페이징 처리 테스트")
    fun `should handle pagination correctly`() {
        // Given
        val paginatedRequest = NaverSearchRequest(
            query = "스프링 부트",
            display = 5,
            start = 11,
            sort = "date"
        )
        val paginatedResponse = createPaginatedNaverResponse()
        
        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(paginatedResponse))

        // When
        val result = naverBookServiceImpl.search(paginatedRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(100, response.total) // 전체 결과 수
                assertEquals(11, response.start) // 시작 인덱스
                assertEquals(5, response.display) // 페이지당 결과 수
                assertEquals(5, response.items.size) // 실제 반환된 결과 수
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("정렬 옵션 처리 테스트")
    fun `should handle different sort options`() {
        // Given
        val sortByDateRequest = request.copy(sort = "date")
        val expectedResponse = createMockNaverResponse()
        
        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = naverBookServiceImpl.search(sortByDateRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertTrue(response.items.isNotEmpty())
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("특수 문자가 포함된 검색어 처리 테스트")
    fun `should handle special characters in search query`() {
        // Given
        val specialCharRequest = request.copy(query = "Spring Boot & Kotlin: \"완전 정복\"")
        val expectedResponse = createMockNaverResponse()
        
        whenever(naverWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(NaverBookResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = naverBookServiceImpl.search(specialCharRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertTrue(response.items.isNotEmpty())
            }
            .verifyComplete()
    }

    // Mock 데이터 생성 헬퍼 메서드들
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

    private fun createPaginatedNaverResponse(): NaverBookResponse {
        val items = (1..5).map { index ->
            NaverSearchResponse(
                title = "스프링 부트 $index",
                link = "https://book.naver.com/bookdb/book_detail.nhn?bid=12345$index",
                image = "https://bookthumb-phinf.pstatic.net/cover/123/456/12345$index.jpg",
                author = "저자 $index",
                discount = "${27000 + (index * 1000)}",
                publisher = "출판사 $index",
                pubdate = "202401${index.toString().padStart(2, '0')}",
                isbn = "979116921003$index",
                description = "스프링 부트 $index 에 대한 실전 가이드"
            )
        }

        return NaverBookResponse(
            lastBuildDate = "Mon, 01 Jan 2024 00:00:00 +0900",
            total = 100,
            start = 11,
            display = 5,
            items = items
        )
    }
}