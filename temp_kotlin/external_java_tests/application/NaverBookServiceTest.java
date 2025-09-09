package com.books.external.application;

import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import com.books.external.api.payload.response.naver.NaverSearchResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NaverBookServiceTest {

    @Mock
    private WebClient naverWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private NaverBookServiceImpl naverBookService;

    private NaverSearchRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(naverBookService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(naverBookService, "clientSecret", "test-client-secret");
        
        request = new NaverSearchRequest("어린왕자", 10, 1);
    }

    @Test
    @DisplayName("네이버 도서 검색 API 호출 성공 테스트")
    void searchBooks_Success() {
        // Given
        NaverBookResponse expectedResponse = createMockNaverResponse();

        when(naverWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NaverBookResponse.class)).thenReturn(Mono.just(expectedResponse));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.total() == 100 &&
                       response.start() == 1 &&
                       response.display() == 10 &&
                       response.items().size() == 2;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("네이버 도서 검색 API 호출 실패 테스트")
    void searchBooks_Error() {
        // Given
        when(naverWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NaverBookResponse.class))
            .thenReturn(Mono.error(new RuntimeException("네이버 API 호출 실패")));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("네이버 API 호출 실패"))
            .verify();
    }

    @Test
    @DisplayName("네이버 검색 결과가 비어있을 때 테스트")
    void searchBooks_EmptyResult() {
        // Given
        NaverBookResponse emptyResponse = new NaverBookResponse(
            "20231201",
            0,
            1,
            10,
            List.of()
        );

        when(naverWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NaverBookResponse.class)).thenReturn(Mono.just(emptyResponse));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.total() == 0 && 
                response.items().isEmpty())
            .verifyComplete();
    }

    @Test
    @DisplayName("네이버 도서 검색 페이징 테스트")
    void searchBooks_Pagination() {
        // Given
        NaverSearchRequest pageRequest = new NaverSearchRequest("어린왕자", 5, 11);
        NaverBookResponse pageResponse = new NaverBookResponse(
            "20231201",
            100,
            11,
            5,
            List.of(createNaverSearchResponse("어린왕자 페이지2", "생텍쥐페리", "테스트출판사"))
        );

        when(naverWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(NaverBookResponse.class)).thenReturn(Mono.just(pageResponse));

        // When
        Mono<NaverBookResponse> result = naverBookService.search(pageRequest);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.start() == 11 && 
                response.display() == 5 &&
                response.items().size() == 1)
            .verifyComplete();
    }

    private NaverBookResponse createMockNaverResponse() {
        NaverSearchResponse item1 = createNaverSearchResponse(
            "어린왕자",
            "생텍쥐페리",
            "김영사"
        );

        NaverSearchResponse item2 = createNaverSearchResponse(
            "어린왕자 컬러링북",
            "생텍쥐페리",
            "미르북스"
        );

        return new NaverBookResponse(
            "20231201",
            100,
            1,
            10,
            Arrays.asList(item1, item2)
        );
    }

    private NaverSearchResponse createNaverSearchResponse(String title, String author, String publisher) {
        return new NaverSearchResponse(
            title,
            "http://book.naver.com/bookdb/book_detail.php?bid=123456",
            "http://bookthumb.phinf.naver.net/cover/123/456/12345678.jpg",
            author,
            "12000",
            "10800",
            publisher,
            "9788932917245",
            "20151015",
            title + "의 상세한 설명입니다. 이 책은 " + author + "가 쓴 명작으로..."
        );
    }
}