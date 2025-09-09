package com.books.external.application

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoDocument
import com.books.external.api.payload.kakao.response.KotlinKakaoMeta
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.application.kakao.KotlinKakaoBooksServiceImpl
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
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class KotlinKakaoBooksServiceTest {

    @Mock
    private lateinit var kakaoWebClient: WebClient

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var kotlinKakaoService: KotlinKakaoBooksServiceImpl

    private lateinit var request: KotlinKakaoSearchRequest

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(kotlinKakaoService, "kakaoAK", "test-kakao-ak")
        request = KotlinKakaoSearchRequest("어린왕자", "title")
    }

    @Test
    @DisplayName("Kotlin 카카오 도서 검색 API 호출 성공 테스트")
    fun searchBooks_Success() {
        // Given
        val expectedResponse = createMockKotlinKakaoResponse()

        whenever(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<java.util.function.Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(KotlinKakaoSearchResponse::class.java))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = kotlinKakaoService.search(request)

        // Then
        assertNotNull(result)
        assertEquals(100, result.meta.totalCount)
        assertEquals(50, result.meta.pageableCount)
        assertEquals(false, result.meta.isEnd)
        assertEquals(2, result.documents.size)
        assertEquals("어린왕자", result.documents[0].title)

        // Verify interactions
        verify(kakaoWebClient).get()
    }

    @Test
    @DisplayName("Kotlin 카카오 검색 결과가 비어있을 때 테스트")
    fun searchBooks_EmptyResult() {
        // Given
        val emptyMeta = KotlinKakaoMeta(0, 0, true)
        val emptyResponse = KotlinKakaoSearchResponse(emptyMeta, emptyList())

        whenever(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<java.util.function.Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(KotlinKakaoSearchResponse::class.java))
            .thenReturn(Mono.just(emptyResponse))

        // When
        val result = kotlinKakaoService.search(request)

        // Then
        assertEquals(0, result.meta.totalCount)
        assertEquals(true, result.meta.isEnd)
        assertEquals(0, result.documents.size)
    }

    @Test
    @DisplayName("Kotlin 카카오 도서 검색 헤더 확인 테스트")
    fun searchBooks_VerifyHeaders() {
        // Given
        val response = createMockKotlinKakaoResponse()
        val headerCaptor = argumentCaptor<String>()

        whenever(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<java.util.function.Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), headerCaptor.capture())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(KotlinKakaoSearchResponse::class.java))
            .thenReturn(Mono.just(response))

        // When
        kotlinKakaoService.search(request)

        // Then
        val capturedHeader = headerCaptor.firstValue
        assertEquals("KakaoAK test-kakao-ak", capturedHeader)
    }

    @Test
    @DisplayName("Kotlin 카카오 도서 검색 마지막 페이지 테스트")
    fun searchBooks_LastPage() {
        // Given
        val lastPageMeta = KotlinKakaoMeta(15, 15, true)
        val doc = KotlinKakaoDocument(
            title = "어린왕자",
            contents = "김영사 편집부",
            publisher = "김영사",
            authors = listOf("생텍쥐페리"),
            datetime = LocalDateTime.of(2015, 10, 15, 0, 0),
            isbn = "9788932917245",
            contents_= "어린왕자의 모험 이야기...",
            thumbnail = "http://image.aladin.co.kr/product/cover.jpg",
            price = 12000,
            status = "NORMAL",
            category = "Book",
            url = "http://search.daum.net/search?q=어린왕자"
        )
        val lastPageResponse = KotlinKakaoSearchResponse(lastPageMeta, listOf(doc))

        whenever(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec)
        whenever(requestHeadersUriSpec.uri(any<java.util.function.Function<*, *>>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.header(any<String>(), any<String>())).thenReturn(requestHeadersSpec)
        whenever(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        whenever(responseSpec.bodyToMono(KotlinKakaoSearchResponse::class.java))
            .thenReturn(Mono.just(lastPageResponse))

        // When
        val result = kotlinKakaoService.search(request)

        // Then
        assertEquals(true, result.meta.isEnd)
        assertEquals(result.meta.totalCount, result.meta.pageableCount)
    }

    private fun createMockKotlinKakaoResponse(): KotlinKakaoSearchResponse {
        val meta = KotlinKakaoMeta(100, 50, false)

        val doc1 = KotlinKakaoDocument(
            title = "어린왕자",
            contents = "김영사 편집부",
            publisher = "김영사",
            authors = listOf("생텍쥐페리"),
            datetime = LocalDateTime.of(2015, 10, 15, 0, 0),
            isbn = "9788932917245",
            contents_ = "어린왕자의 모험 이야기. 사막에서 만난 어린왕자와 조종사의 대화를 통해...",
            thumbnail = "http://image.aladin.co.kr/product/12345/cover500.jpg",
            price = 12000,
            status = "NORMAL",
            category = "Book",
            url = "http://search.daum.net/search?q=어린왕자"
        )

        val doc2 = KotlinKakaoDocument(
            title = "어린왕자 컬러링북",
            contents = "미르북스 편집부",
            publisher = "미르북스",
            authors = listOf("생텍쥐페리"),
            datetime = LocalDateTime.of(2020, 3, 1, 0, 0),
            isbn = "9791188850273",
            contents_ = "어린왕자의 아름다운 장면들을 직접 색칠해 볼 수 있는 컬러링북...",
            thumbnail = "http://image.aladin.co.kr/product/78901/cover500.jpg",
            price = 8000,
            status = "NORMAL",
            category = "Book",
            url = "http://search.daum.net/search?q=어린왕자+컬러링북"
        )

        return KotlinKakaoSearchResponse(meta, listOf(doc1, doc2))
    }
}