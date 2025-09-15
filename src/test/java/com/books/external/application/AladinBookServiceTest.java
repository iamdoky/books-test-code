package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
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
class AladinBookServiceTest {

    private static MockWebServer mockWebServer;
    private AladinBookServiceImpl aladinBookService;
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

        aladinBookService = new AladinBookServiceImpl(webClient);
        setTtbKey(aladinBookService, "test-ttb-key");

        objectMapper = new ObjectMapper();
    }

    @Test
    void search_성공적으로_알라딘_API_호출() throws JsonProcessingException, InterruptedException {
        // Given
        AladinBookRequest request = new AladinBookRequest(
                "Spring Boot",
                "Title",
                "10",
                "1",
                "Book",
                "SalesPoint",
                "js",
                "20131101"
        );

        AladinBookResponse expectedResponse = new AladinBookResponse(
                "20131101",
                "http://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
                "알라딘 검색결과 - Spring Boot",
                "http://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=Book&SearchWord=Spring+Boot&x=23&y=11",
                "Wed, 06 Nov 2024 17:34:14 GMT",
                145,
                1,
                "Spring Boot",
                0,
                "전체",
                List.of()
        );

        String responseBody = objectMapper.writeValueAsString(expectedResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        // When
        Mono<AladinBookResponse> result = aladinBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).contains("/ttb/api/ItemSearch.aspx");
        assertThat(recordedRequest.getPath()).contains("ttbkey=test-ttb-key");
        assertThat(recordedRequest.getPath()).contains("Query=Spring%20Boot");
    }

    @Test
    void search_API_호출_실패시_에러_반환() {
        // Given
        AladinBookRequest request = new AladinBookRequest(
                "Spring Boot",
                "Title",
                "10",
                "1",
                "Book",
                "SalesPoint",
                "js",
                "20131101"
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When
        Mono<AladinBookResponse> result = aladinBookService.search(request);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify(Duration.ofSeconds(5));
    }

    private void setTtbKey(AladinBookServiceImpl service, String ttbKey) {
        try {
            var field = AladinBookServiceImpl.class.getDeclaredField("ttbKey");
            field.setAccessible(true);
            field.set(service, ttbKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}