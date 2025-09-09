package com.books.external.application;

import static org.junit.jupiter.api.Assertions.*;

package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.aladin.AladinSearchResponse;
import com.books.external.api.payload.response.aladin.SeriesInfo;
import com.books.external.api.payload.response.aladin.SubInfo;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AladinBookServiceTest {

    @Mock
    private WebClient aladinWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AladinBookServiceImpl aladinBookService;

    private AladinBookRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aladinBookService, "ttbKey", "test-ttb-key");
        
        request = new AladinBookRequest(
            "어린왕자",
            "Keyword",
            "10",
            "1",
            "Book",
            "PublishTime",
            "JS",
            "20131101"
        );
    }

    @Test
    @DisplayName("알라딘 도서 검색 API 호출 성공 테스트")
    void searchBooks_Success() {
        // Given
        AladinBookResponse expectedResponse = createMockAladinResponse();

        when(aladinWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AladinBookResponse.class)).thenReturn(Mono.just(expectedResponse));

        // When
        Mono<AladinBookResponse> result = aladinBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> {
                return response.title().equals("알라딘 검색결과") &&
                       response.totalResults() == 100 &&
                       response.startIndex() == 1 &&
                       response.item().size() == 2;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("알라딘 도서 검색 API 호출 실패 테스트")
    void searchBooks_Error() {
        // Given
        when(aladinWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AladinBookResponse.class))
            .thenReturn(Mono.error(new RuntimeException("API 호출 실패")));

        // When
        Mono<AladinBookResponse> result = aladinBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectErrorMatches(throwable -> 
                throwable instanceof RuntimeException && 
                throwable.getMessage().equals("API 호출 실패"))
            .verify();
    }

    @Test
    @DisplayName("알라딘 검색 결과가 비어있을 때 테스트")
    void searchBooks_EmptyResult() {
        // Given
        AladinBookResponse emptyResponse = new AladinBookResponse(
            "알라딘 검색결과",
            "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx",
            "20131101",
            "success",
            0,
            1,
            10,
            List.of()
        );

        when(aladinWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AladinBookResponse.class)).thenReturn(Mono.just(emptyResponse));

        // When
        Mono<AladinBookResponse> result = aladinBookService.search(request);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> response.totalResults() == 0 && response.item().isEmpty())
            .verifyComplete();
    }

    private AladinBookResponse createMockAladinResponse() {
        SubInfo subInfo1 = new SubInfo(
            "9788932917245",
            "8932917248",
            "어린왕자",
            "김영사",
            "2015-10-15",
            "328",
            "어린왕자 상세 정보"
        );

        SubInfo subInfo2 = new SubInfo(
            "9791188850273",
            "1188850277",
            "어린왕자 컬러링북",
            "미르북스",
            "2020-03-01",
            "120",
            "컬러링북 상세 정보"
        );

        SeriesInfo seriesInfo = new SeriesInfo("어린왕자 시리즈", "1");

        AladinSearchResponse item1 = new AladinSearchResponse(
            "어린왕자",
            "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123456",
            "생텍쥐페리",
            "12000",
            "10800",
            "10%",
            "http://image.aladin.co.kr/product/12345/cover500.jpg",
            "5점",
            "2015-10-15",
            "9788932917245",
            "8932917248",
            "어린왕자의 모험 이야기",
            "김영사",
            "성인",
            "Book",
            subInfo1,
            seriesInfo
        );

        AladinSearchResponse item2 = new AladinSearchResponse(
            "어린왕자 컬러링북",
            "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=789012",
            "생텍쥐페리",
            "8000",
            "7200",
            "10%",
            "http://image.aladin.co.kr/product/78901/cover500.jpg",
            "4.5점",
            "2020-03-01",
            "9791188850273",
            "1188850277",
            "어린왕자 컬러링북",
            "미르북스",
            "아동",
            "Book",
            subInfo2,
            seriesInfo
        );

        return new AladinBookResponse(
            "알라딘 검색결과",
            "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx",
            "20131101",
            "success",
            100,
            1,
            10,
            Arrays.asList(item1, item2)
        );
    }
}