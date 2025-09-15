package com.books.book.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KotlinBookServiceTest {

    private val kotlinBookService: KotlinBookService = KotlinBookServiceImpl()

    @Test
    fun `getBookNameByIsbn_올바른_ISBN으로_책_이름_반환`() {
        // Given
        val isbn = "9788966260959"

        // When
        val result = kotlinBookService.getBookNameByIsbn(isbn)

        // Then
        assertThat(result).isEqualTo("클린코드")
    }

    @Test
    fun `getBookNameByIsbn_다른_ISBN으로도_동일한_책_이름_반환`() {
        // Given
        val isbn = "9788966261000"

        // When
        val result = kotlinBookService.getBookNameByIsbn(isbn)

        // Then
        assertThat(result).isEqualTo("클린코드")
    }

    @Test
    fun `getBookNameByIsbn_빈_문자열_ISBN으로_책_이름_반환`() {
        // Given
        val isbn = ""

        // When
        val result = kotlinBookService.getBookNameByIsbn(isbn)

        // Then
        assertThat(result).isEqualTo("클린코드")
    }
}