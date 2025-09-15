package com.books.external.application;

import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.kakao.KakaoMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KakaoBookServiceTest {

    private static MockWebServer mockWebServer;
    private KakaoBookServiceImpl kakaoBookService;
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        kakaoBookService = new KakaoBookServiceImpl(webClient);
        setKakaoAK(kakaoBookService, "test-kakao-ak");

        objectMapper = new ObjectMapper();
    }

    @Test
    void search_성공적으로_카카오_API_호출() throws JsonProcessingException, InterruptedException {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("Spring Boot", "title");

        KakaoBookResponse expectedResponse = new KakaoBookResponse(
                List.of(),
                new KakaoMeta(false, 50, 100)
        );

        String responseBody = objectMapper.writeValueAsString(expectedResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).contains("/v3/search/book");
        assertThat(recordedRequest.getPath()).contains("query=Spring%20Boot");
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("KakaoAK test-kakao-ak");
    }

    @Test
    void search_API_호출_실패시_에러_반환() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("Spring Boot", "title");

        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify(Duration.ofSeconds(5));
    }

    private void setKakaoAK(KakaoBookServiceImpl service, String kakaoAK) {
        try {
            var field = KakaoBookServiceImpl.class.getDeclaredField("kakaoAK");
            field.setAccessible(true);
            field.set(service, kakaoAK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}