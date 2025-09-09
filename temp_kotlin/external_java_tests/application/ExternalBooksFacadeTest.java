package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.aladin.AladinSearchResponse;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.kakao.KakaoDocument;
import com.books.external.api.payload.response.kakao.KakaoMeta;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import com.books.external.api.payload.response.naver.NaverSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalBooksFacadeTest {

    @Mock
    private AladinBookService aladinBookService;

    @Mock
    private KakaoBookService kakaoBookService;

    @Mock
    private NaverBookService naverBookService;

    @InjectMocks
    private ExternalBooksFacade externalBooksFacade;

    private AladinBookRequest aladinRequest;
    private KakaoSearchRequest kakaoRequest;
    private NaverSearchRequest naverRequest;

    @BeforeEach
    void setUp() {
        aladinRequest = new AladinBookRequest(
            "어린왕자", "Keyword", "10", "1", "Book", "PublishTime", "JS", "20131101"
        );
        
        kakaoRequest = new KakaoSearchRequest("어린왕자", "title");
        
        naverRequest = new NaverSearchRequest("어린왕자", 10, 1);
    }

    @Test
    @DisplayName("알라딘 도서 검색 Facade 테스트")
    void searchAladinBooks_Success() {
        // Given
        AladinBookResponse expectedResponse = new AladinBookResponse(
            "알라딘 검색결과",
            "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx",
            "20131101",
            "success",
            10,
            1,
            10,
            List.of()
        );

        when(aladinBookService.search(any(AladinBookRequest.class)))
            .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<AladinBookResponse> result = externalBooksFacade.search(aladinRequest);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.title().equals("알라딘 검색결과") &&
                       response.totalResults() == 10;
            })
            .verifyComplete();

        verify(aladinBookService).search(aladinRequest);
    }

    @Test
    @DisplayName("카카오 도서 검색 Facade 테스트")
    void searchKakaoBooks_Success() {
        // Given
        KakaoMeta meta = new KakaoMeta(50, 30, false);
        KakaoDocument document = new KakaoDocument(
            "어린왕자",
            "김영사 편집부",
            "김영사",
            List.of("생텍쥐페리"),
            LocalDateTime.of(2015, 10, 15, 0, 0),
            "9788932917245",
            "어린왕자의 모험 이야기",
            "http://image.aladin.co.kr/product/cover.jpg",
            12000,
            "NORMAL",
            "Book",
            "http://search.daum.net/search?q=어린왕자"
        );
        KakaoBookResponse expectedResponse = new KakaoBookResponse(meta, List.of(document));

        when(kakaoBookService.search(any(KakaoSearchRequest.class)))
            .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<KakaoBookResponse> result = externalBooksFacade.search(kakaoRequest);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.meta().total_count() == 50 &&
                       response.documents().size() == 1 &&
                       response.documents().get(0).title().equals("어린왕자");
            })
            .verifyComplete();

        verify(kakaoBookService).search(kakaoRequest);
    }

    @Test
    @DisplayName("네이버 도서 검색 Facade 테스트")
    void searchNaverBooks_Success() {
        // Given
        NaverSearchResponse item = new NaverSearchResponse(
            "어린왕자",
            "http://book.naver.com/bookdb/book_detail.php?bid=123456",
            "http://bookthumb.phinf.naver.net/cover/123/456/12345678.jpg",
            "생텍쥐페리",
            "12000",
            "10800",
            "김영사",
            "9788932917245",
            "20151015",
            "어린왕자의 상세한 설명입니다."
        );
        NaverBookResponse expectedResponse = new NaverBookResponse(
            "20231201", 25, 1, 10, List.of(item)
        );

        when(naverBookService.search(any(NaverSearchRequest.class)))
            .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<NaverBookResponse> result = externalBooksFacade.search(naverRequest);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.total() == 25 &&
                       response.items().size() == 1 &&
                       response.items().get(0).title().equals("어린왕자");
            })
            .verifyComplete();

        verify(naverBookService).search(naverRequest);
    }

    @Test
    @DisplayName("알라딘 도서 검색 Facade 실패 테스트")
    void searchAladinBooks_Error() {
        // Given
        when(aladinBookService.search(any(AladinBookRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("알라딘 서비스 오류")));

        // When
        Mono<AladinBookResponse> result = externalBooksFacade.search(aladinRequest);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("알라딘 서비스 오류"))
            .verify();

        verify(aladinBookService).search(aladinRequest);
    }

    @Test
    @DisplayName("카카오 도서 검색 Facade 실패 테스트")
    void searchKakaoBooks_Error() {
        // Given
        when(kakaoBookService.search(any(KakaoSearchRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("카카오 서비스 오류")));

        // When
        Mono<KakaoBookResponse> result = externalBooksFacade.search(kakaoRequest);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("카카오 서비스 오류"))
            .verify();

        verify(kakaoBookService).search(kakaoRequest);
    }

    @Test
    @DisplayName("네이버 도서 검색 Facade 실패 테스트")
    void searchNaverBooks_Error() {
        // Given
        when(naverBookService.search(any(NaverSearchRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("네이버 서비스 오류")));

        // When
        Mono<NaverBookResponse> result = externalBooksFacade.search(naverRequest);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("네이버 서비스 오류"))
            .verify();

        verify(naverBookService).search(naverRequest);
    }
}