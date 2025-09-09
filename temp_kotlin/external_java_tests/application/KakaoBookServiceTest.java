package com.books.external.application;

import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.kakao.KakaoDocument;
import com.books.external.api.payload.response.kakao.KakaoMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KakaoBookServiceTest {

    @Mock
    private WebClient kakaoWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private KakaoBookServiceImpl kakaoBookService;

    private KakaoSearchRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoBookService, "kakaoAK", "test-kakao-ak");
        
        request = new KakaoSearchRequest("어린왕자", "title");
    }

    @Test
    @DisplayName("카카오 도서 검색 API 호출 성공 테스트")
    void searchBooks_Success() {
        // Given
        KakaoBookResponse expectedResponse = createMockKakaoResponse();

        when(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoBookResponse.class)).thenReturn(Mono.just(expectedResponse));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.meta().total_count() == 100 &&
                       response.meta().pageable_count() == 50 &&
                       response.meta().is_end() == false &&
                       response.documents().size() == 2;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("카카오 도서 검색 API 호출 실패 테스트")
    void searchBooks_Error() {
        // Given
        when(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoBookResponse.class))
            .thenReturn(Mono.error(new RuntimeException("카카오 API 호출 실패")));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("카카오 API 호출 실패"))
            .verify();
    }

    @Test
    @DisplayName("카카오 검색 결과가 비어있을 때 테스트")
    void searchBooks_EmptyResult() {
        // Given
        KakaoMeta emptyMeta = new KakaoMeta(0, 0, true);
        KakaoBookResponse emptyResponse = new KakaoBookResponse(emptyMeta, List.of());

        when(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoBookResponse.class)).thenReturn(Mono.just(emptyResponse));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.meta().total_count() == 0 && 
                response.documents().isEmpty())
            .verifyComplete();
    }

    @Test
    @DisplayName("카카오 도서 검색 결과 마지막 페이지 테스트")
    void searchBooks_LastPage() {
        // Given
        KakaoMeta lastPageMeta = new KakaoMeta(15, 15, true);
        KakaoDocument doc = new KakaoDocument(
            "어린왕자",
            "김영사 편집부",
            "김영사",
            List.of("생텍쥐페리"),
            LocalDateTime.of(2015, 10, 15, 0, 0),
            "9788932917245",
            "어린왕자의 모험 이야기...",
            "http://image.aladin.co.kr/product/cover.jpg",
            12000,
            "NORMAL",
            "Book",
            "http://search.daum.net/search?q=어린왕자"
        );
        KakaoBookResponse lastPageResponse = new KakaoBookResponse(lastPageMeta, List.of(doc));

        when(kakaoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoBookResponse.class)).thenReturn(Mono.just(lastPageResponse));

        // When
        Mono<KakaoBookResponse> result = kakaoBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.meta().is_end() == true && 
                response.meta().total_count() == response.meta().pageable_count())
            .verifyComplete();
    }

    private KakaoBookResponse createMockKakaoResponse() {
        KakaoMeta meta = new KakaoMeta(100, 50, false);

        KakaoDocument doc1 = new KakaoDocument(
            "어린왕자",
            "김영사 편집부",
            "김영사",
            List.of("생텍쥐페리"),
            LocalDateTime.of(2015, 10, 15, 0, 0),
            "9788932917245",
            "어린왕자의 모험 이야기. 사막에서 만난 어린왕자와 조종사의 대화를 통해...",
            "http://image.aladin.co.kr/product/12345/cover500.jpg",
            12000,
            "NORMAL",
            "Book",
            "http://search.daum.net/search?q=어린왕자"
        );

        KakaoDocument doc2 = new KakaoDocument(
            "어린왕자 컬러링북",
            "미르북스 편집부",
            "미르북스",
            List.of("생텍쥐페리"),
            LocalDateTime.of(2020, 3, 1, 0, 0),
            "9791188850273",
            "어린왕자의 아름다운 장면들을 직접 색칠해 볼 수 있는 컬러링북...",
            "http://image.aladin.co.kr/product/78901/cover500.jpg",
            8000,
            "NORMAL",
            "Book",
            "http://search.daum.net/search?q=어린왕자+컬러링북"
        );

        return new KakaoBookResponse(meta, Arrays.asList(doc1, doc2));
    }
}