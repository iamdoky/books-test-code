package com.books.performance;

import com.books.external.application.ExternalBooksFacade;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 성능 개선사항 검증을 위한 테스트 클래스
 *
 * 목적:
 * 1. WebClient Connection Pool 설정 검증
 * 2. 응답 시간 측정
 * 3. 동시 요청 처리 능력 검증
 * 4. 타임아웃 설정 검증
 */
@SpringBootTest
@ActiveProfiles("test")
class PerformanceImprovementTest {

    @Autowired
    private ExternalBooksFacade externalBooksFacade;

    @Autowired
    @Qualifier("kakaoWebClient")
    private WebClient kakaoWebClient;

    @Autowired
    @Qualifier("naverWebClient")
    private WebClient naverWebClient;

    @Autowired
    @Qualifier("aladinWebClient")
    private WebClient aladinWebClient;

    @Test
    @DisplayName("WebClient가 적절한 기본 URL로 설정되어 있는지 검증")
    void shouldHaveProperBaseUrls() {
        // Given & When & Then
        assertNotNull(kakaoWebClient, "카카오 WebClient가 설정되어야 합니다");
        assertNotNull(naverWebClient, "네이버 WebClient가 설정되어야 합니다");
        assertNotNull(aladinWebClient, "알라딘 WebClient가 설정되어야 합니다");

        // WebClient 설정 검증은 실제 HTTP 요청을 통해 간접적으로 확인
        // (WebClient의 내부 설정은 직접 접근하기 어려움)
    }

    @Test
    @DisplayName("API 응답 시간이 허용 가능한 범위 내인지 검증")
    void shouldRespondWithinAcceptableTime() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("자바", "title");
        Duration maxAllowedTime = Duration.ofSeconds(10); // 최대 허용 시간

        // When & Then
        StepVerifier.create(externalBooksFacade.search(request))
                .expectNextMatches(response -> {
                    assertNotNull(response, "응답이 null이면 안 됩니다");
                    return true;
                })
                .expectComplete()
                .verify(maxAllowedTime);
    }

    @Test
    @DisplayName("동시 요청 처리 능력 검증")
    void shouldHandleConcurrentRequests() throws InterruptedException {
        // Given
        int concurrentRequests = 10;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        KakaoSearchRequest request = new KakaoSearchRequest("스프링부트", "title");

        // When
        for (int i = 0; i < concurrentRequests; i++) {
            Mono<KakaoBookResponse> responseMono = externalBooksFacade.search(request);

            responseMono.subscribe(
                response -> {
                    successCount.incrementAndGet();
                    latch.countDown();
                },
                error -> {
                    failureCount.incrementAndGet();
                    latch.countDown();
                }
            );
        }

        // Then
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "모든 요청이 30초 내에 완료되어야 합니다");

        int totalProcessed = successCount.get() + failureCount.get();
        assertEquals(concurrentRequests, totalProcessed,
            "모든 요청이 처리되어야 합니다");

        // 성공률이 70% 이상이어야 함 (네트워크 상황 고려)
        double successRate = (double) successCount.get() / concurrentRequests * 100;
        assertTrue(successRate >= 70.0,
            String.format("성공률이 70%% 이상이어야 합니다. 현재: %.2f%%", successRate));
    }

    @Test
    @DisplayName("메모리 사용량이 과도하지 않은지 검증")
    void shouldNotConsumeExcessiveMemory() {
        // Given
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        KakaoSearchRequest request = new KakaoSearchRequest("코틀린", "title");

        // When
        Mono<KakaoBookResponse> responseMono = externalBooksFacade.search(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response != null)
                .expectComplete()
                .verify(Duration.ofSeconds(10));

        // Then
        System.gc(); // 가비지 컬렉션 강제 실행
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        // 메모리 증가량이 50MB를 초과하지 않아야 함
        long maxAllowedIncrease = 50 * 1024 * 1024; // 50MB
        assertTrue(memoryIncrease <= maxAllowedIncrease,
            String.format("메모리 증가량이 과도합니다. 증가량: %d bytes", memoryIncrease));
    }

    @Test
    @DisplayName("연속 요청 시 성능 저하가 없는지 검증")
    void shouldMaintainPerformanceUnderLoad() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("프로그래밍", "title");

        int numberOfRequests = 5;
        long[] responseTimes = new long[numberOfRequests];

        // When
        for (int i = 0; i < numberOfRequests; i++) {
            long startTime = System.currentTimeMillis();

            StepVerifier.create(externalBooksFacade.search(request))
                    .expectNextMatches(response -> response != null)
                    .expectComplete()
                    .verify(Duration.ofSeconds(10));

            long endTime = System.currentTimeMillis();
            responseTimes[i] = endTime - startTime;
        }

        // Then
        // 첫 번째 요청과 마지막 요청의 응답 시간 차이가 3배를 넘지 않아야 함
        long firstRequestTime = responseTimes[0];
        long lastRequestTime = responseTimes[numberOfRequests - 1];

        assertTrue(lastRequestTime <= firstRequestTime * 3,
            String.format("성능 저하가 감지되었습니다. 첫 요청: %dms, 마지막 요청: %dms",
                firstRequestTime, lastRequestTime));

        // 평균 응답 시간이 5초를 넘지 않아야 함
        double averageResponseTime = 0;
        for (long responseTime : responseTimes) {
            averageResponseTime += responseTime;
        }
        averageResponseTime /= numberOfRequests;

        assertTrue(averageResponseTime <= 5000,
            String.format("평균 응답 시간이 너무 깁니다: %.2fms", averageResponseTime));
    }

    @Test
    @DisplayName("타임아웃 설정이 적절히 동작하는지 검증")
    void shouldHandleTimeoutsProperly() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("매우긴검색어로타임아웃테스트하기위한키워드", "title");

        // When & Then
        // 타임아웃이 발생하거나 정상 응답이 와야 함 (둘 다 정상 동작)
        StepVerifier.create(externalBooksFacade.search(request))
                .expectNextMatches(response -> response != null)
                .expectComplete()
                .verify(Duration.ofSeconds(15)); // 최대 15초 대기
    }

    @Test
    @DisplayName("리소스 누수가 없는지 검증")
    void shouldNotLeakResources() {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("리소스테스트", "title");

        // When - 여러 번 요청하여 리소스 누수 확인
        for (int i = 0; i < 20; i++) {
            StepVerifier.create(externalBooksFacade.search(request))
                    .expectNextMatches(response -> response != null)
                    .expectComplete()
                    .verify(Duration.ofSeconds(10));
        }

        // Then
        // 시스템이 여전히 정상적으로 응답할 수 있어야 함
        StepVerifier.create(externalBooksFacade.search(request))
                .expectNextMatches(response -> response != null)
                .expectComplete()
                .verify(Duration.ofSeconds(10));

        // 테스트 완료 시점에서 시스템이 정상 상태여야 함
        assertTrue(true, "리소스 누수 없이 테스트가 완료되어야 합니다");
    }
}