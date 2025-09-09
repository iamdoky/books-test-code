package com.books.external.application

import com.books.external.api.payload.request.aladin.AladinBookRequest
import com.books.external.api.payload.request.kakao.KakaoSearchRequest
import com.books.external.api.payload.request.naver.NaverSearchRequest
import com.books.external.api.payload.response.aladin.AladinBookResponse
import com.books.external.api.payload.response.aladin.AladinSearchResponse
import com.books.external.api.payload.response.kakao.KakaoBookResponse
import com.books.external.api.payload.response.kakao.KakaoDocument
import com.books.external.api.payload.response.kakao.KakaoMeta
import com.books.external.api.payload.response.naver.NaverBookResponse
import com.books.external.api.payload.response.naver.NaverSearchResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * ExternalBooksFacade의 Kotlin 테스트 스위트
 * 목표: Java ExternalBooksFacade를 Kotlin으로 변환하기 위한 TDD 테스트
 */
@ExtendWith(MockitoExtension::class)
class KotlinExternalBooksFacadeTest {

    @Mock
    private lateinit var aladinBookService: AladinBookService

    @Mock
    private lateinit var kakaoBookService: KakaoBookService

    @Mock
    private lateinit var naverBookService: NaverBookService

    @InjectMocks
    private lateinit var externalBooksFacade: ExternalBooksFacade

    private lateinit var aladinRequest: AladinBookRequest
    private lateinit var kakaoRequest: KakaoSearchRequest
    private lateinit var naverRequest: NaverSearchRequest

    @BeforeEach
    fun setUp() {
        aladinRequest = AladinBookRequest(
            query = "어린왕자",
            queryType = "Keyword",
            maxResults = "10",
            start = "1",
            searchTarget = "Book",
            sort = "PublishTime",
            output = "JS",
            version = "20131101"
        )

        kakaoRequest = KakaoSearchRequest("코틀린 인 액션")

        naverRequest = NaverSearchRequest(
            query = "스프링 부트",
            display = 10,
            start = 1,
            sort = "sim"
        )
    }

    @Test
    @DisplayName("알라딘 도서 검색 파사드 호출 성공 테스트")
    fun `should successfully delegate Aladin book search to service`() {
        // Given
        val expectedResponse = createMockAladinResponse()
        whenever(aladinBookService.search(any<AladinBookRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = externalBooksFacade.search(aladinRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals("어린왕자", response.query)
                assertEquals(1, response.totalResults)
                assertEquals("알라딘 검색결과 - 어린왕자", response.title)
            }
            .verifyComplete()

        verify(aladinBookService).search(aladinRequest)
    }

    @Test
    @DisplayName("카카오 도서 검색 파사드 호출 성공 테스트")
    fun `should successfully delegate Kakao book search to service`() {
        // Given
        val expectedResponse = createMockKakaoResponse()
        whenever(kakaoBookService.search(any<KakaoSearchRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = externalBooksFacade.search(kakaoRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(100, response.meta.totalCount)
                assertEquals(1, response.documents.size)
                assertEquals("코틀린 인 액션", response.documents[0].title)
            }
            .verifyComplete()

        verify(kakaoBookService).search(kakaoRequest)
    }

    @Test
    @DisplayName("네이버 도서 검색 파사드 호출 성공 테스트")
    fun `should successfully delegate Naver book search to service`() {
        // Given
        val expectedResponse = createMockNaverResponse()
        whenever(naverBookService.search(any<NaverSearchRequest>()))
            .thenReturn(Mono.just(expectedResponse))

        // When
        val result = externalBooksFacade.search(naverRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(1, response.total)
                assertEquals(1, response.items.size)
                assertEquals("스프링 부트 실전 활용 마스터", response.items[0].title)
            }
            .verifyComplete()

        verify(naverBookService).search(naverRequest)
    }

    @Test
    @DisplayName("알라딘 서비스 오류 시 파사드 예외 처리 테스트")
    fun `should handle Aladin service error gracefully`() {
        // Given
        val errorMessage = "Aladin API connection failed"
        whenever(aladinBookService.search(any<AladinBookRequest>()))
            .thenReturn(Mono.error(RuntimeException(errorMessage)))

        // When
        val result = externalBooksFacade.search(aladinRequest)

        // Then
        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is RuntimeException && throwable.message == errorMessage
            }
            .verify()

        verify(aladinBookService).search(aladinRequest)
    }

    @Test
    @DisplayName("카카오 서비스 오류 시 파사드 예외 처리 테스트")
    fun `should handle Kakao service error gracefully`() {
        // Given
        val errorMessage = "Kakao API rate limit exceeded"
        whenever(kakaoBookService.search(any<KakaoSearchRequest>()))
            .thenReturn(Mono.error(RuntimeException(errorMessage)))

        // When
        val result = externalBooksFacade.search(kakaoRequest)

        // Then
        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is RuntimeException && throwable.message == errorMessage
            }
            .verify()

        verify(kakaoBookService).search(kakaoRequest)
    }

    @Test
    @DisplayName("네이버 서비스 오류 시 파사드 예외 처리 테스트")
    fun `should handle Naver service error gracefully`() {
        // Given
        val errorMessage = "Naver API authentication failed"
        whenever(naverBookService.search(any<NaverSearchRequest>()))
            .thenReturn(Mono.error(RuntimeException(errorMessage)))

        // When
        val result = externalBooksFacade.search(naverRequest)

        // Then
        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is RuntimeException && throwable.message == errorMessage
            }
            .verify()

        verify(naverBookService).search(naverRequest)
    }

    @Test
    @DisplayName("빈 응답 처리 테스트")
    fun `should handle empty responses from services`() {
        // Given
        val emptyAladinResponse = AladinBookResponse(
            version = "20131101",
            logo = "",
            title = "검색결과 없음",
            link = "",
            pubDate = "",
            totalResults = 0,
            startIndex = 1,
            query = "존재하지않는책",
            searchCategoryId = 0,
            searchCategoryName = "통합검색",
            item = emptyList()
        )

        whenever(aladinBookService.search(any<AladinBookRequest>()))
            .thenReturn(Mono.just(emptyAladinResponse))

        // When
        val result = externalBooksFacade.search(aladinRequest)

        // Then
        StepVerifier.create(result)
            .assertNext { response ->
                assertNotNull(response)
                assertEquals(0, response.totalResults)
                assertEquals(0, response.item.size)
            }
            .verifyComplete()
    }

    // Mock 데이터 생성 헬퍼 메서드들
    private fun createMockAladinResponse(): AladinBookResponse {
        return AladinBookResponse(
            version = "20131101",
            logo = "https://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
            title = "알라딘 검색결과 - 어린왕자",
            link = "https://www.aladin.co.kr/search/wsearchresult.aspx",
            pubDate = "Mon, 01 Jan 2024 00:00:00 GMT",
            totalResults = 1,
            startIndex = 1,
            query = "어린왕자",
            searchCategoryId = 0,
            searchCategoryName = "통합검색",
            item = listOf(
                AladinSearchResponse(
                    title = "어린왕자",
                    link = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123456",
                    author = "생텍쥐페리",
                    pubDate = "2024-01-01",
                    description = "어린왕자에 대한 설명",
                    isbn = "9788937460013",
                    isbn13 = "9788937460013",
                    itemId = 123456,
                    priceSales = 10000,
                    priceStandard = 12000,
                    mallType = "BOOK",
                    stockStatus = "재고있음",
                    mileage = 100,
                    cover = "https://image.aladin.co.kr/product/123456/cover.jpg",
                    categoryId = 1,
                    categoryName = "문학",
                    publisher = "민음사",
                    salesPoint = 1000,
                    adult = false,
                    fixedPrice = true,
                    customerReviewRank = 9.5,
                    bestDuration = "",
                    bestRank = 0,
                    seriesInfo = null,
                    subInfo = null
                )
            )
        )
    }

    private fun createMockKakaoResponse(): KakaoBookResponse {
        return KakaoBookResponse(
            meta = KakaoMeta(
                totalCount = 100,
                pageableCount = 50,
                isEnd = false
            ),
            documents = listOf(
                KakaoDocument(
                    title = "코틀린 인 액션",
                    contents = "코틀린에 대한 상세한 설명",
                    url = "https://search.daum.net/search?q=코틀린",
                    isbn = "9791161750712",
                    datetime = LocalDateTime.of(2024, 1, 1, 0, 0),
                    authors = listOf("드미트리 제메로프", "스베트라나 이사코바"),
                    publisher = "에이콘출판사",
                    translators = listOf("오현석"),
                    price = 36000,
                    salePrice = 32400,
                    thumbnail = "https://search1.kakaocdn.net/thumb/cover.jpg",
                    status = "정상판매"
                )
            )
        )
    }

    private fun createMockNaverResponse(): NaverBookResponse {
        return NaverBookResponse(
            lastBuildDate = "Mon, 01 Jan 2024 00:00:00 +0900",
            total = 1,
            start = 1,
            display = 10,
            items = listOf(
                NaverSearchResponse(
                    title = "스프링 부트 실전 활용 마스터",
                    link = "https://book.naver.com/bookdb/book_detail.nhn?bid=123456",
                    image = "https://bookthumb-phinf.pstatic.net/cover/123/456/12345678.jpg",
                    author = "그렉 턴키스트",
                    discount = "27000",
                    publisher = "한빛미디어",
                    pubdate = "20240101",
                    isbn = "9791169210034",
                    description = "스프링 부트에 대한 실전 가이드"
                )
            )
        )
    }
}