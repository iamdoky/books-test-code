package com.books.book.application

import com.books.book.domain.Book
import com.books.book.entity.BookEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

// Mock Repository Interface for testing
interface MockBookRepository : JpaRepository<BookEntity, Long> {
    fun findByTitle(title: String): List<BookEntity>
}

@ExtendWith(MockitoExtension::class)
class KotlinBookServiceTest {

    @Mock
    private lateinit var bookRepository: MockBookRepository

    @InjectMocks
    private lateinit var kotlinBookService: KotlinBookServiceImpl

    private lateinit var sampleBook: Book
    private lateinit var sampleBookEntity: BookEntity

    @BeforeEach
    fun setUp() {
        sampleBook = Book(
            id = 1L,
            title = "어린왕자",
            author = "생텍쥐페리",
            publisher = "김영사",
            price = 12000,
            isbn = "9788932917245"
        )
        
        sampleBookEntity = BookEntity(
            id = 1L,
            title = "어린왕자",
            author = "생텍쥐페리",
            publisher = "김영사",
            price = 12000,
            isbn = "9788932917245"
        )
    }

    @Test
    @DisplayName("Kotlin 도서 저장 테스트")
    fun saveBook_Success() {
        // Given
        whenever(bookRepository.save(org.mockito.kotlin.any())).thenReturn(sampleBookEntity)

        // When
        val result = kotlinBookService.saveBook(sampleBook)

        // Then
        assertNotNull(result)
        assertEquals("어린왕자", result.title)
        assertEquals("생텍쥐페리", result.author)
        assertEquals("김영사", result.publisher)
        assertEquals(12000, result.price)
        assertEquals("9788932917245", result.isbn)

        verify(bookRepository).save(org.mockito.kotlin.any())
    }

    @Test
    @DisplayName("Kotlin 도서 ID로 조회 테스트")
    fun getBookById_Success() {
        // Given
        whenever(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(sampleBookEntity))

        // When
        val result = kotlinBookService.getBookById(1L)

        // Then
        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("어린왕자", result?.title)
        assertEquals("생텍쥐페리", result?.author)

        verify(bookRepository).findById(1L)
    }

    @Test
    @DisplayName("Kotlin 도서 ID로 조회 - 존재하지 않는 경우 테스트")
    fun getBookById_NotFound() {
        // Given
        whenever(bookRepository.findById(999L)).thenReturn(java.util.Optional.empty())

        // When
        val result = kotlinBookService.getBookById(999L)

        // Then
        assertEquals(null, result)

        verify(bookRepository).findById(999L)
    }

    @Test
    @DisplayName("Kotlin 모든 도서 조회 테스트")
    fun getAllBooks_Success() {
        // Given
        val bookEntities = listOf(
            sampleBookEntity,
            BookEntity(2L, "해리포터", "J.K.롤링", "문학수첩", 15000, "9788932917246")
        )
        whenever(bookRepository.findAll()).thenReturn(bookEntities)

        // When
        val result = kotlinBookService.getAllBooks()

        // Then
        assertEquals(2, result.size)
        assertEquals("어린왕자", result[0].title)
        assertEquals("해리포터", result[1].title)

        verify(bookRepository).findAll()
    }

    @Test
    @DisplayName("Kotlin 도서 제목으로 검색 테스트")
    fun findBooksByTitle_Success() {
        // Given
        val bookEntities = listOf(sampleBookEntity)
        whenever(bookRepository.findByTitle("어린왕자")).thenReturn(bookEntities)

        // When
        val result = kotlinBookService.findBooksByTitle("어린왕자")

        // Then
        assertEquals(1, result.size)
        assertEquals("어린왕자", result[0].title)
        assertEquals("생텍쥐페리", result[0].author)

        verify(bookRepository).findByTitle("어린왕자")
    }

    @Test
    @DisplayName("Kotlin 도서 제목으로 검색 - 결과 없음 테스트")
    fun findBooksByTitle_NotFound() {
        // Given
        whenever(bookRepository.findByTitle("존재하지않는책")).thenReturn(emptyList())

        // When
        val result = kotlinBookService.findBooksByTitle("존재하지않는책")

        // Then
        assertEquals(0, result.size)

        verify(bookRepository).findByTitle("존재하지않는책")
    }

    @Test
    @DisplayName("Kotlin 도서 삭제 테스트")
    fun deleteBook_Success() {
        // Given
        whenever(bookRepository.existsById(1L)).thenReturn(true)

        // When
        val result = kotlinBookService.deleteBook(1L)

        // Then
        assertEquals(true, result)

        verify(bookRepository).existsById(1L)
        verify(bookRepository).deleteById(1L)
    }

    @Test
    @DisplayName("Kotlin 도서 삭제 - 존재하지 않는 경우 테스트")
    fun deleteBook_NotFound() {
        // Given
        whenever(bookRepository.existsById(999L)).thenReturn(false)

        // When
        val result = kotlinBookService.deleteBook(999L)

        // Then
        assertEquals(false, result)

        verify(bookRepository).existsById(999L)
        verify(bookRepository, org.mockito.kotlin.never()).deleteById(999L)
    }

    @Test
    @DisplayName("Kotlin 도서 데이터 클래스 속성 테스트")
    fun bookDataClass_PropertiesTest() {
        // Given & When
        val book = Book(
            id = 123L,
            title = "코틀린 완벽 가이드",
            author = "알렉세이 세드니오프",
            publisher = "에이콘출판",
            price = 35000,
            isbn = "9791161754775"
        )

        // Then - 데이터 클래스의 특성 확인
        assertEquals(123L, book.id)
        assertEquals("코틀린 완벽 가이드", book.title)
        assertEquals("알렉세이 세드니오프", book.author)
        assertEquals("에이콘출판", book.publisher)
        assertEquals(35000, book.price)
        assertEquals("9791161754775", book.isbn)

        // copy 함수 테스트
        val updatedBook = book.copy(price = 30000)
        assertEquals(30000, updatedBook.price)
        assertEquals("코틀린 완벽 가이드", updatedBook.title) // 다른 속성은 그대로 유지

        // toString 테스트 (데이터 클래스 자동 생성)
        val bookString = book.toString()
        assert(bookString.contains("코틀린 완벽 가이드"))
        assert(bookString.contains("35000"))
    }
}