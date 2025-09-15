package com.books.external.application;

import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.naver.NaverBookResponse;
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
class NaverBookServiceTest {

    private static MockWebServer mockWebServer;
    private NaverBookServiceImpl naverBookService;
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

        naverBookService = new NaverBookServiceImpl(webClient);
        setClientId(naverBookService, "test-client-id");
        setClientSecret(naverBookService, "test-client-secret");

        objectMapper = new ObjectMapper();
    }

    @Test
    void search_성공적으로_네이버_API_호출() throws JsonProcessingException, InterruptedException {
        // Given
        NaverSearchRequest request = new NaverSearchRequest("Spring Boot", 10, 1);

        NaverBookResponse expectedResponse = new NaverBookResponse(
                "Wed, 06 Nov 2024 17:34:14 +0900",
                100,
                1,
                10,
                List.of()
        );

        String responseBody = objectMapper.writeValueAsString(expectedResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).contains("/v1/search/book.json");
        assertThat(recordedRequest.getPath()).contains("query=Spring%20Boot");
        assertThat(recordedRequest.getHeader("X-Naver-Client-Id")).isEqualTo("test-client-id");
        assertThat(recordedRequest.getHeader("X-Naver-Client-Secret")).isEqualTo("test-client-secret");
    }

    @Test
    void search_API_호출_실패시_에러_반환() {
        // Given
        NaverSearchRequest request = new NaverSearchRequest("Spring Boot", 10, 1);

        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify(Duration.ofSeconds(5));
    }

    private void setClientId(NaverBookServiceImpl service, String clientId) {
        try {
            var field = NaverBookServiceImpl.class.getDeclaredField("clientId");
            field.setAccessible(true);
            field.set(service, clientId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setClientSecret(NaverBookServiceImpl service, String clientSecret) {
        try {
            var field = NaverBookServiceImpl.class.getDeclaredField("clientSecret");
            field.setAccessible(true);
            field.set(service, clientSecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}