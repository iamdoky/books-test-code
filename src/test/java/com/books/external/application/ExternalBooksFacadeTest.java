package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalBooksFacadeTest {

    @Mock
    private AladinBookService aladinBookService;

    @Mock
    private KakaoBookService kakaoBookService;

    @Mock
    private NaverBookService naverBookService;

    private ExternalBooksFacade externalBooksFacade;

    @BeforeEach
    void setUp() {
        externalBooksFacade = new ExternalBooksFacade(
                aladinBookService,
                kakaoBookService,
                naverBookService
        );
    }

    @Test
    void search_알라딘_서비스_정상_호출() {
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
                "http://www.aladin.co.kr/search/wsearchresult.aspx",
                "Wed, 06 Nov 2024 17:34:14 GMT",
                145,
                1,
                "Spring Boot",
                0,
                "전체",
                java.util.List.of()
        );

        when(aladinBookService.search(any(AladinBookRequest.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<AladinBookResponse> result = externalBooksFacade.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void search_카카오_서비스_정상_호출() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("Spring Boot", "title");

        KakaoBookResponse expectedResponse = new KakaoBookResponse(
                java.util.List.of(),
                new com.books.external.api.payload.response.kakao.KakaoMeta(false, 50, 100)
        );

        when(kakaoBookService.search(any(KakaoSearchRequest.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<KakaoBookResponse> result = externalBooksFacade.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void search_네이버_서비스_정상_호출() {
        // Given
        NaverSearchRequest request = new NaverSearchRequest("Spring Boot", 10, 1);

        NaverBookResponse expectedResponse = new NaverBookResponse(
                "Wed, 06 Nov 2024 17:34:14 +0900",
                100,
                1,
                10,
                java.util.List.of()
        );

        when(naverBookService.search(any(NaverSearchRequest.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<NaverBookResponse> result = externalBooksFacade.search(request);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}