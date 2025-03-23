package com.books.book.api

import com.books.book.application.KotlinBookService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "[코틀린] 테스트")
@RestController
@RequestMapping("/kotlin/api/books")
class KotlinBookController(private val service: KotlinBookService) {

    @GetMapping("/name")
    fun getBookName(@RequestParam isbn: String): ResponseEntity<String> {

        return ResponseEntity.ok(service.getBookNameByIsbn(isbn))
    }
}