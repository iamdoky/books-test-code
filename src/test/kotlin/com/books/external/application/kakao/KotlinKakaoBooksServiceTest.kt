package com.books.external.application.kakao

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.kakao.response.KotlinKakaoMeta
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.io.IOException
import java.time.Duration

@SpringBootTest
class KotlinKakaoBooksServiceTest {

    private lateinit var kotlinKakaoBooksService: KotlinKakaoBooksServiceImpl
    private lateinit var objectMapper: ObjectMapper

    companion object {
        private lateinit var mockWebServer: MockWebServer

        @BeforeAll
        @JvmStatic
        fun setUp() {
            mockWebServer = MockWebServer()
            mockWebServer.start()
        }

        @AfterAll
        @JvmStatic
        @Throws(IOException::class)
        fun tearDown() {
            mockWebServer.shutdown()
        }
    }

    @BeforeEach
    fun initialize() {
        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        kotlinKakaoBooksService = KotlinKakaoBooksServiceImpl(webClient)
        setKakaoAK(kotlinKakaoBooksService, "test-kakao-ak")

        objectMapper = ObjectMapper()
    }

    @Test
    fun `search_성공적으로_카카오_API_호출_코루틴`() = runBlocking {
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

        val responseBody = objectMapper.writeValueAsString(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val result = kotlinKakaoBooksService.search(request)

        // Then
        assertThat(result).isEqualTo(expectedResponse)

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.method).isEqualTo("GET")
        assertThat(recordedRequest.path).contains("/v3/search/book")
        assertThat(recordedRequest.path).contains("query=Spring%20Boot")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("KakaoAK test-kakao-ak")
    }

    @Test
    fun `searchMono_성공적으로_카카오_API_호출_Mono`() {
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

        val responseBody = objectMapper.writeValueAsString(expectedResponse)

        mockWebServer.enqueue(
            MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json")
        )

        // When
        val result = kotlinKakaoBooksService.searchMono(request)

        // Then
        StepVerifier.create(result)
            .expectNext(expectedResponse)
            .verifyComplete()
    }

    @Test
    fun `searchMono_API_호출_실패시_에러_반환`() {
        // Given
        val request = KotlinKakaoSearchRequest("Spring Boot", "title")

        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        // When
        val result = kotlinKakaoBooksService.searchMono(request)

        // Then
        StepVerifier.create(result)
            .expectError()
            .verify(Duration.ofSeconds(5))
    }

    private fun setKakaoAK(service: KotlinKakaoBooksServiceImpl, kakaoAK: String) {
        try {
            val field = KotlinKakaoBooksServiceImpl::class.java.getDeclaredField("kakaoAK")
            field.isAccessible = true
            field.set(service, kakaoAK)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}